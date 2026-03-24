# Apache Camel y Red Hat Fuse

---

## 1. Que es Apache Camel?

Framework de integracion open source que implementa los **Enterprise Integration Patterns (EIP)**. Permite conectar sistemas heterogeneos usando rutas declarativas.

```
Sistema A → [Ruta Camel] → Transformacion → [Ruta Camel] → Sistema B
```

**Red Hat Fuse** = distribucion empresarial de Apache Camel con soporte Red Hat + componentes adicionales (Fuse on EAP, Fuse on OpenShift).

| Concepto | Que es |
|----------|--------|
| **Route** | Flujo de integracion desde un endpoint de entrada hasta uno de salida |
| **Endpoint** | Punto de conexion (URI) a un sistema externo (HTTP, JMS, File, JDBC...) |
| **Exchange** | Objeto que viaja por la ruta conteniendo mensaje (headers + body) |
| **Processor** | Componente que transforma o procesa el Exchange |
| **Component** | Plugin que conecta Camel con una tecnologia (camel-http, camel-jms, camel-cxf) |
| **DSL** | Lenguaje para definir rutas (Java DSL, XML DSL, YAML DSL) |
| **Camel Context** | Contenedor principal que gestiona rutas, componentes y lifecycle |

---

## 2. Anatomia de una ruta Camel

```java
// Java DSL
@Component
public class MiRuta extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Manejo de errores global
        errorHandler(deadLetterChannel("jms:queue:errores")
            .maximumRedeliveries(3)
            .redeliveryDelay(2000));

        // Ruta principal
        from("jms:queue:solicitudes-credito")       // Consumer endpoint (entrada)
            .routeId("procesarSolicitudCredito")
            .log("Recibida solicitud: ${body}")
            .unmarshal().json(JsonLibrary.Jackson, SolicitudCredito.class) // JSON → Object
            .process(new ValidarSolicitudProcessor())  // Logica custom
            .choice()                                   // Content-Based Router
                .when(simple("${body.monto} > 50000"))
                    .to("direct:aprobacionGerencia")
                .when(simple("${body.score} < 500"))
                    .to("direct:rechazarSolicitud")
                .otherwise()
                    .to("direct:aprobacionAutomatica")
            .end()
            .marshal().json()                           // Object → JSON
            .to("jms:queue:solicitudes-procesadas");    // Producer endpoint (salida)
    }
}
```

```xml
<!-- XML DSL (Blueprint - comun en Fuse on Karaf) -->
<camelContext xmlns="http://camel.apache.org/schema/blueprint">
    <route id="procesarSolicitudCredito">
        <from uri="jms:queue:solicitudes-credito"/>
        <log message="Recibida solicitud: ${body}"/>
        <unmarshal>
            <json library="Jackson" unmarshalType="com.banco.SolicitudCredito"/>
        </unmarshal>
        <choice>
            <when>
                <simple>${body.monto} > 50000</simple>
                <to uri="direct:aprobacionGerencia"/>
            </when>
            <otherwise>
                <to uri="direct:aprobacionAutomatica"/>
            </otherwise>
        </choice>
    </route>
</camelContext>
```

---

## 3. Componentes mas usados en integracion financiera

| Componente | URI | Uso tipico |
|------------|-----|------------|
| **camel-http/camel-http4** | `http4://host/api` | Llamadas REST a BTServices |
| **camel-cxf** | `cxf:bean:miServicio` | Consumir/exponer servicios SOAP |
| **camel-jms** | `jms:queue:nombre` | Mensajeria con ActiveMQ/AMQ |
| **camel-kafka** | `kafka:topicName` | Eventos asincronos |
| **camel-jdbc** | `jdbc:dataSource` | Consultas SQL directas |
| **camel-sql** | `sql:SELECT * FROM...` | Consultas SQL parametrizadas |
| **camel-file** | `file:/ruta/directorio` | Leer/escribir archivos (batch) |
| **camel-ftp** | `ftp://host/dir` | Transferencia de archivos |
| **camel-jackson** | marshal/unmarshal | Conversion JSON |
| **camel-jaxb** | marshal/unmarshal | Conversion XML (SOAP) |
| **camel-rest** | `rest("/api")` | Exponer APIs REST |
| **camel-timer** | `timer:nombre?period=60000` | Ejecucion periodica (batch nocturno) |
| **camel-direct** | `direct:nombre` | Conectar rutas internas (sincrono) |
| **camel-seda** | `seda:nombre` | Conectar rutas internas (asincrono) |

```java
// Ejemplo: Consumir API REST de Bantotal
from("timer:consultaSaldos?period=300000")
    .setHeader(Exchange.HTTP_METHOD, constant("POST"))
    .setHeader("Content-Type", constant("application/json"))
    .setBody(simple("{\"Btinreq\":{\"Canal\":\"CAMEL\"},\"Cuenta\":\"${header.cuenta}\"}"))
    .to("http4://bantotal-host:8080/BTServices/api/Cuentas/ObtenerSaldo")
    .unmarshal().json(JsonLibrary.Jackson)
    .log("Saldo: ${body[saldo]}");
```

---

## 4. Transformacion de datos

```java
// 1. MARSHAL / UNMARSHAL (serializacion)
from("direct:entrada")
    .unmarshal().json(JsonLibrary.Jackson, MiClase.class) // JSON string → Objeto Java
    .marshal().json();                                      // Objeto Java → JSON string

// 2. PROCESSOR (transformacion custom)
.process(exchange -> {
    SolicitudCredito solicitud = exchange.getIn().getBody(SolicitudCredito.class);
    // Transformar al formato que espera Bantotal
    BantotalRequest btRequest = new BantotalRequest();
    btRequest.setMonto(solicitud.getMontoSolicitado());
    btRequest.setClienteId(solicitud.getDni());
    exchange.getIn().setBody(btRequest);
})

// 3. BEAN (delegar a un servicio Spring)
.bean(transformadorService, "convertirAFormatoBT")

// 4. SIMPLE LANGUAGE (expresiones inline)
.setBody(simple("Hola ${header.nombre}, tu solicitud ${body.id} fue recibida"))

// 5. DATA FORMAT con JAXB (para SOAP/XML)
JaxbDataFormat jaxb = new JaxbDataFormat("com.banco.modelo.xml");
from("direct:soapRequest")
    .marshal(jaxb)   // Objeto → XML
    .to("cxf:bean:servicioSoap");

// 6. CONVERTIR TIPOS
.convertBodyTo(String.class)
.convertBodyTo(byte[].class)
```

---

## 5. Manejo de errores en Camel

```java
@Override
public void configure() throws Exception {

    // ERROR HANDLER GLOBAL: Dead Letter Channel
    errorHandler(deadLetterChannel("jms:queue:DLQ.errores")
        .maximumRedeliveries(3)
        .redeliveryDelay(5000)
        .retryAttemptedLogLevel(LoggingLevel.WARN)
        .useExponentialBackOff()
        .backOffMultiplier(2));

    // EXCEPCIONES ESPECIFICAS con onException
    onException(BantotalTimeoutException.class)
        .maximumRedeliveries(5)
        .redeliveryDelay(10000)
        .handled(true)                    // No propagar al caller
        .log(LoggingLevel.ERROR, "Timeout llamando a Bantotal: ${exception.message}")
        .to("jms:queue:reintentos-bantotal");

    onException(ValidationException.class)
        .handled(true)
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
        .setBody(simple("{\"error\": \"${exception.message}\"}"))
        .log("Validacion fallida: ${exception.message}");

    // DO-TRY / DO-CATCH (try-catch inline en la ruta)
    from("direct:operacionCritica")
        .doTry()
            .to("http4://bantotal/api/operacion")
        .doCatch(HttpOperationFailedException.class)
            .log("Error HTTP: ${exception.statusCode}")
            .to("direct:fallback")
        .doFinally()
            .log("Operacion finalizada")
        .end();
}
```

---

## 6. Testing de rutas Camel

```java
@CamelSpringBootTest
@SpringBootTest
public class SolicitudCreditoRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:result")
    private MockEndpoint mockResult;

    @Test
    public void debeRutearSolicitudAlta() throws Exception {
        // Reemplazar endpoint real por mock
        AdviceWith.adviceWith(camelContext, "procesarSolicitudCredito", a -> {
            a.weaveByToUri("jms:queue:solicitudes-procesadas")
             .replace().to("mock:result");
        });

        // Configurar expectativa
        mockResult.expectedMessageCount(1);

        // Enviar mensaje de prueba
        SolicitudCredito solicitud = new SolicitudCredito();
        solicitud.setMonto(75000);
        solicitud.setScore(700);
        producerTemplate.sendBody("jms:queue:solicitudes-credito", solicitud);

        // Verificar
        mockResult.assertIsSatisfied();
    }
}
```

---

## 7. Camel con Spring Boot

```java
// application.yml
camel:
  springboot:
    main-run-controller: true   # Mantener app corriendo
  component:
    jms:
      connection-factory: "#connectionFactory"

// Dependencias clave (pom.xml)
// camel-spring-boot-starter
// camel-jms-starter
// camel-jackson-starter
// camel-cxf-starter
// camel-http-starter
```

```java
// Configuracion de ruta REST con Camel
@Component
public class RestApiRoute extends RouteBuilder {
    @Override
    public void configure() {
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true")
            .contextPath("/api/v1");

        rest("/creditos")
            .get("/{id}")
                .to("direct:obtenerCredito")
            .post("/")
                .type(SolicitudCredito.class)
                .to("direct:crearSolicitud");

        from("direct:obtenerCredito")
            .bean(creditoService, "buscarPorId(${header.id})")
            .marshal().json();
    }
}
```

---

## 8. Red Hat Fuse - especificos

| Concepto | Descripcion |
|----------|-------------|
| **Fuse on EAP** | Camel corriendo dentro de JBoss EAP (war/ear deployment) |
| **Fuse on Karaf** | Camel corriendo en OSGi container (bundles, Blueprint XML) |
| **Fuse on OpenShift** | Camel en contenedores (Spring Boot + S2I builds) |
| **Fuse Console** | UI web (Hawtio) para monitorear rutas, ver metricas, trazar mensajes |
| **Fabric8** | Plataforma de gestion para Fuse en Kubernetes/OpenShift |
| **Blueprint XML** | Formato de configuracion para OSGi (alternativa a Spring XML en Karaf) |

---

## Preguntas de entrevista

### Basicas
1. **Que es Apache Camel y para que sirve?**
   - Framework de integracion que implementa EIP para conectar sistemas heterogeneos mediante rutas declarativas
2. **Cual es la diferencia entre `direct:` y `seda:`?**
   - `direct`: sincrono, mismo hilo. `seda`: asincrono, cola en memoria, hilos separados
3. **Que es un Exchange en Camel?**
   - Objeto que contiene el mensaje (In/Out), headers, properties y el patron de intercambio (InOnly/InOut)
4. **Diferencia entre Java DSL y XML DSL?**
   - Java DSL: mas type-safe, mejor IDE support, refactoring. XML DSL: cambios sin recompilar, Blueprint para OSGi
5. **Que es el Dead Letter Channel?**
   - Patron donde mensajes que fallan despues de N reintentos se envian a una cola de errores para revision

### Intermedias
6. **Como manejas transacciones en una ruta Camel?**
   - TransactionPolicy (REQUIRED, REQUIRES_NEW), PlatformTransactionManager, marcar ruta como `.transacted()`
7. **Diferencia entre Fuse on EAP vs Fuse on Karaf?**
   - EAP: Java EE completo, deploy como WAR/EAR. Karaf: OSGi, bundles hot-deploy, mas liviano
8. **Como haces testing de rutas Camel?**
   - CamelTestSupport, MockEndpoint, AdviceWith para reemplazar endpoints, ProducerTemplate
9. **Que es el Content-Based Router y como lo implementas?**
   - EIP que rutea mensajes segun su contenido. `.choice().when(predicate).to(endpoint).otherwise().to(otro)`
10. **Como monitoreas rutas en produccion?**
    - Fuse Console (Hawtio), JMX MBeans, metricas Prometheus con camel-micrometer, trazas con OpenTracing

### Avanzadas
11. **Como garantizas el orden de mensajes en Camel?**
    - Usar una sola particion (Kafka), SEDA con un solo consumidor, o Resequencer EIP
12. **Como manejas idempotencia para evitar procesar duplicados?**
    - IdempotentConsumer con repositorio (memoria, JDBC, Infinispan): `.idempotentConsumer(header("idOperacion"), repo)`
13. **Como implementarias un circuit breaker en Camel?**
    - `.circuitBreaker().to("http4://servicio-externo").onFallback().to("direct:fallback")`
14. **Como migras de Camel 2 a Camel 3/4?**
    - Paquetes `org.apache.camel` cambian, componentes renombrados (http4→http), XML namespace actualizado, quitar APIs deprecated

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| Que es Camel? | Framework de integracion basado en EIP |
| Que es Red Hat Fuse? | Distribucion empresarial de Camel con soporte Red Hat |
| direct vs seda? | direct=sincrono, seda=asincrono (cola en memoria) |
| Como manejas errores? | errorHandler, onException, doTry/doCatch, Dead Letter Channel |
| Que es un Exchange? | Objeto que viaja por la ruta: headers + body + properties |
| Componentes clave? | camel-http, camel-cxf, camel-jms, camel-kafka, camel-jackson |
| Como testeas rutas? | MockEndpoint + AdviceWith + ProducerTemplate |
| Fuse on EAP vs Karaf? | EAP=Java EE (WAR), Karaf=OSGi (bundles) |
| Que es Hawtio? | Consola web para monitorear Camel (Fuse Console) |
| Idempotencia? | IdempotentConsumer con repo para evitar duplicados |

# SOAP y Servicios Web

---

## 1. Que es SOAP?

**Simple Object Access Protocol**: protocolo de comunicacion basado en XML para intercambiar informacion entre sistemas. Muy usado en el sector financiero y en Bantotal.

```
[Cliente SOAP]                                    [Servidor SOAP]
     │                                                  │
     │  1. Obtener WSDL (contrato)                      │
     │←─────────────────────────────────────────────────│
     │                                                  │
     │  2. Enviar SOAP Request (XML envelope)           │
     │─────────────────────────────────────────────────→│
     │                                                  │
     │  3. Recibir SOAP Response (XML envelope)         │
     │←─────────────────────────────────────────────────│
```

| Concepto | Descripcion |
|----------|-------------|
| **SOAP** | Protocolo basado en XML para intercambio de mensajes |
| **WSDL** | Web Services Description Language: contrato que describe el servicio |
| **Envelope** | Elemento raiz XML que envuelve el mensaje SOAP |
| **Header** | Metadatos opcionales (seguridad, transacciones) |
| **Body** | Contenido del mensaje (request o response) |
| **Fault** | Estructura para errores en SOAP |
| **Endpoint** | URL donde se expone el servicio |
| **Operation** | Funcion/metodo que expone el servicio |
| **Binding** | Protocolo de transporte (generalmente HTTP) |

---

## 2. Estructura SOAP

```xml
<!-- SOAP Request -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ban="http://bantotal.com/servicios">
    <soapenv:Header>
        <!-- WS-Security header (autenticacion) -->
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
            <wsse:UsernameToken>
                <wsse:Username>USR_MW</wsse:Username>
                <wsse:Password>secret123</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soapenv:Header>
    <soapenv:Body>
        <ban:ObtenerCliente>
            <ban:clienteId>12345</ban:clienteId>
        </ban:ObtenerCliente>
    </soapenv:Body>
</soapenv:Envelope>

<!-- SOAP Response -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <ban:ObtenerClienteResponse>
            <ban:cliente>
                <ban:id>12345</ban:id>
                <ban:nombre>Juan Perez</ban:nombre>
                <ban:dni>12345678</ban:dni>
                <ban:tipo>PERSONA_NATURAL</ban:tipo>
            </ban:cliente>
        </ban:ObtenerClienteResponse>
    </soapenv:Body>
</soapenv:Envelope>

<!-- SOAP Fault (Error) -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <soapenv:Fault>
            <faultcode>soapenv:Client</faultcode>
            <faultstring>Cliente no encontrado</faultstring>
            <detail>
                <ban:ErrorDetail>
                    <ban:codigo>404</ban:codigo>
                    <ban:mensaje>No existe cliente con ID 12345</ban:mensaje>
                </ban:ErrorDetail>
            </detail>
        </soapenv:Fault>
    </soapenv:Body>
</soapenv:Envelope>
```

---

## 3. WSDL (Web Services Description Language)

```xml
<!-- Estructura basica de un WSDL -->
<definitions name="BTClientesService"
             xmlns="http://schemas.xmlsoap.org/wsdl/"
             xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
             xmlns:tns="http://bantotal.com/servicios"
             targetNamespace="http://bantotal.com/servicios">

    <!-- TYPES: estructuras de datos (XML Schema) -->
    <types>
        <xs:schema targetNamespace="http://bantotal.com/servicios">
            <xs:element name="ObtenerCliente">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="clienteId" type="xs:long"/>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
            <xs:element name="ObtenerClienteResponse">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="cliente" type="tns:Cliente"/>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
        </xs:schema>
    </types>

    <!-- MESSAGE: mensajes de entrada y salida -->
    <message name="ObtenerClienteRequest">
        <part name="parameters" element="tns:ObtenerCliente"/>
    </message>
    <message name="ObtenerClienteResponse">
        <part name="parameters" element="tns:ObtenerClienteResponse"/>
    </message>

    <!-- PORT TYPE: operaciones (como una interfaz Java) -->
    <portType name="BTClientesPortType">
        <operation name="ObtenerCliente">
            <input message="tns:ObtenerClienteRequest"/>
            <output message="tns:ObtenerClienteResponse"/>
        </operation>
    </portType>

    <!-- BINDING: protocolo de transporte -->
    <binding name="BTClientesBinding" type="tns:BTClientesPortType">
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
        <operation name="ObtenerCliente">
            <soap:operation soapAction="ObtenerCliente"/>
            <input><soap:body use="literal"/></input>
            <output><soap:body use="literal"/></output>
        </operation>
    </binding>

    <!-- SERVICE: ubicacion del servicio -->
    <service name="BTClientesService">
        <port name="BTClientesPort" binding="tns:BTClientesBinding">
            <soap:address location="http://bantotal:8080/BTServices/ws/BTClientes"/>
        </port>
    </service>
</definitions>
```

---

## 4. Generar clases Java desde WSDL

```xml
<!-- pom.xml - Plugin para generar clases desde WSDL -->
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-codegen-plugin</artifactId>
    <version>3.6.2</version>
    <executions>
        <execution>
            <id>generate-sources</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>wsdl2java</goal>
            </goals>
            <configuration>
                <wsdlOptions>
                    <wsdlOption>
                        <wsdl>${basedir}/src/main/resources/wsdl/BTClientes.wsdl</wsdl>
                        <wsdlLocation>classpath:wsdl/BTClientes.wsdl</wsdlLocation>
                        <extraargs>
                            <extraarg>-p</extraarg>
                            <extraarg>com.banco.btservices.clientes</extraarg>
                        </extraargs>
                    </wsdlOption>
                </wsdlOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 5. Apache CXF con Camel

### Consumir servicio SOAP

```java
// Configuracion del endpoint CXF
@Configuration
public class CxfConfig {

    @Bean
    public CxfEndpoint btClientesEndpoint() {
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress("http://bantotal:8080/BTServices/ws/BTClientes");
        endpoint.setServiceClass(BTClientesPortType.class);
        endpoint.setWsdlURL("classpath:wsdl/BTClientes.wsdl");
        endpoint.setDataFormat(DataFormat.POJO);

        // WS-Security
        Map<String, Object> properties = new HashMap<>();
        properties.put("ws-security.username", "USR_MW");
        properties.put("ws-security.password", "secret");
        properties.put("ws-security.callback-handler", new PasswordCallbackHandler());
        endpoint.setProperties(properties);

        return endpoint;
    }
}

// Ruta Camel que consume el servicio SOAP
@Component
public class ClienteSoapRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:obtenerCliente")
            .process(exchange -> {
                Long clienteId = exchange.getIn().getBody(Long.class);
                ObtenerCliente request = new ObtenerCliente();
                request.setClienteId(clienteId);
                exchange.getIn().setBody(new Object[]{request});
                exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "ObtenerCliente");
            })
            .to("cxf:bean:btClientesEndpoint")
            .process(exchange -> {
                MessageContentsList response = exchange.getIn().getBody(MessageContentsList.class);
                ObtenerClienteResponse result = (ObtenerClienteResponse) response.get(0);
                exchange.getIn().setBody(result.getCliente());
            });
    }
}
```

### Exponer servicio SOAP

```java
// Exponer un servicio SOAP propio
@WebService(serviceName = "MiddlewareService",
            portName = "MiddlewarePort",
            targetNamespace = "http://banco.com/middleware")
public interface MiddlewareService {

    @WebMethod(operationName = "ConsultarSaldo")
    @WebResult(name = "saldo")
    SaldoResponse consultarSaldo(
        @WebParam(name = "cuentaId") Long cuentaId
    );
}

// Implementacion
@WebService(endpointInterface = "com.banco.middleware.MiddlewareService")
public class MiddlewareServiceImpl implements MiddlewareService {

    @Override
    public SaldoResponse consultarSaldo(Long cuentaId) {
        // Logica de negocio
        return saldoService.consultar(cuentaId);
    }
}

// Exponer con Camel CXF
from("cxf:bean:middlewareEndpoint")
    .routeId("middlewareSoapService")
    .choice()
        .when(header(CxfConstants.OPERATION_NAME).isEqualTo("ConsultarSaldo"))
            .to("direct:consultarSaldo")
    .end();
```

---

## 6. SOAP vs REST

| Aspecto | SOAP | REST |
|---------|------|------|
| **Formato** | XML obligatorio | JSON, XML, otros |
| **Contrato** | WSDL (estricto) | OpenAPI/Swagger (opcional) |
| **Protocolo** | SOAP sobre HTTP, JMS, SMTP | HTTP |
| **Seguridad** | WS-Security (mensaje level) | OAuth2, JWT (transporte level) |
| **Transacciones** | WS-AtomicTransaction | Saga pattern |
| **Estado** | Stateful posible | Stateless |
| **Tooling** | Generacion de codigo desde WSDL | Mas manual |
| **Rendimiento** | Mas pesado (XML) | Mas ligero (JSON) |
| **Uso en banca** | Legacy, operaciones criticas | Nuevos desarrollos, APIs publicas |

---

## 7. WS-Security

```java
// Interceptor de seguridad para CXF
@Configuration
public class WsSecurityConfig {

    @Bean
    public WSS4JOutInterceptor wss4jOutInterceptor() {
        Map<String, Object> props = new HashMap<>();
        props.put(WSHandlerConstants.ACTION, "UsernameToken Timestamp");
        props.put(WSHandlerConstants.USER, "USR_MW");
        props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        props.put(WSHandlerConstants.PW_CALLBACK_CLASS,
                  PasswordCallbackHandler.class.getName());
        return new WSS4JOutInterceptor(props);
    }
}

// Password Callback Handler
public class PasswordCallbackHandler implements CallbackHandler {
    @Override
    public void handle(Callback[] callbacks) {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                if ("USR_MW".equals(pc.getIdentifier())) {
                    pc.setPassword("secretPassword");
                }
            }
        }
    }
}
```

---

## Preguntas de entrevista

### Basicas
1. **Que es SOAP?**
   - Protocolo de comunicacion basado en XML para intercambio de mensajes entre sistemas
2. **Que es un WSDL?**
   - Contrato XML que describe el servicio: operaciones, tipos de datos, binding y ubicacion
3. **Cuales son las partes de un mensaje SOAP?**
   - Envelope (raiz), Header (metadatos), Body (contenido), Fault (errores)
4. **Diferencia entre SOAP y REST?**
   - SOAP: XML, WSDL, WS-Security, mas formal. REST: JSON, HTTP verbs, mas ligero y flexible

### Intermedias
5. **Como generas clases Java desde un WSDL?**
   - cxf-codegen-plugin (wsdl2java) o wsimport (JAX-WS). Genera stubs, types y service interfaces
6. **Que es WS-Security?**
   - Estandar para seguridad a nivel de mensaje SOAP: autenticacion (UsernameToken), cifrado, firma digital
7. **Como consumes un servicio SOAP en Camel?**
   - Componente camel-cxf con CxfEndpoint configurado, dataFormat POJO o PAYLOAD
8. **Que es MTOM?**
   - Message Transmission Optimization Mechanism: enviar binarios (PDF, imagenes) en SOAP sin base64

### Avanzadas
9. **Contract-first vs Code-first: cual prefieres y por que?**
   - Contract-first (WSDL primero): mejor para integracion, clientes pueden generar codigo. Code-first: mas rapido para prototipos
10. **Como manejas versionado de servicios SOAP?**
    - Namespace versionado, WSDL separados por version, compatibilidad backward

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| Que es SOAP? | Protocolo XML para comunicacion entre sistemas |
| WSDL? | Contrato que describe el servicio (operaciones, tipos, endpoint) |
| Partes del mensaje? | Envelope, Header, Body, Fault |
| SOAP vs REST? | SOAP=XML/formal/WS-Security, REST=JSON/ligero/HTTP |
| wsdl2java? | Genera clases Java desde WSDL (CXF o JAX-WS) |
| WS-Security? | Seguridad a nivel de mensaje (auth, cifrado, firma) |
| CXF en Camel? | camel-cxf component con CxfEndpoint |
| Contract-first? | Disenar WSDL primero, luego generar codigo |
| MTOM? | Envio eficiente de binarios en SOAP |
| Fault? | Estructura estandar para errores en respuesta SOAP |

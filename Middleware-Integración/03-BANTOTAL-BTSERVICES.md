# Bantotal y BTServices

---

## 1. Que es Bantotal?

**Bantotal** es un **core bancario** (software que gestiona las operaciones centrales de un banco o entidad financiera). Es el sistema mas utilizado en Latinoamerica por bancos, cajas municipales, cooperativas y microfinancieras.

```
[Canales]          [Middleware]              [Core Bancario]
App Movil  ───→                         ┌─────────────────┐
Web Banking ──→  Apache Camel / Fuse ──→│   BANTOTAL       │
Agencias   ───→                         │  - Cuentas       │
ATM        ───→                         │  - Creditos      │
                                        │  - Clientes      │
                                        │  - Operaciones   │
                                        └─────────────────┘
```

| Concepto | Descripcion |
|----------|-------------|
| **Core bancario** | Sistema central que gestiona cuentas, creditos, clientes, transacciones |
| **Bantotal** | Producto core bancario de la empresa Technisys (ahora Globant) |
| **BTServices** | Capa de servicios (API) que expone Bantotal para integracion |
| **Btinreq** | Cabecera obligatoria en toda peticion a BTServices (canal, usuario, token) |
| **SdtXxx** | Prefijo para estructuras de datos complejas en BTServices |

---

## 2. BTServices - Capa de Servicios

BTServices expone las operaciones de Bantotal como **servicios REST y SOAP**.

### Estructura de una peticion REST

```json
// POST http://bantotal-host:8080/BTServices/api/Prestamos/Simular
{
    "Btinreq": {
        "Canal": "MIDDLEWARE",
        "Requerimiento": "1",
        "Usuario": "USR_MIDDLEWARE",
        "Token": "abc123def456",
        "Device": "SERVER"
    },
    "sdtPrestamo": {
        "productoUId": 1234,
        "fechaPrimerPago": "2026-04-15",
        "monto": 50000.00,
        "cantidadCuotas": 12,
        "periodoCuotas": 30,
        "tasa": 0
    }
}
```

### Estructura de una respuesta REST

```json
{
    "Btoutreq": {
        "Canal": "MIDDLEWARE",
        "Servicio": "BTPrestamos.Simular",
        "Fecha": "2026-03-23",
        "Hora": "14:30:00",
        "Numero": 12345,
        "Estado": "OK",
        "Requerimiento": "1"
    },
    "sdtSimulacion": {
        "totalIntereses": 8500.00,
        "totalCapital": 50000.00,
        "totalCuota": 58500.00,
        "cft": 0.17,
        "tea": 0.15,
        "cronograma": [
            {
                "nroCuota": 1,
                "fechaVencimiento": "2026-04-15",
                "capital": 4166.67,
                "interes": 708.33,
                "cuota": 4875.00,
                "saldo": 45833.33
            }
        ]
    },
    "Erroresnegocio": [],
    "Btoutreq": { "Estado": "OK" }
}
```

### Estructura SOAP (WSDL)

```xml
<!-- Request SOAP a BTServices -->
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bt="http://uy.com.dlya.bantotal/BTSOA/">
    <soapenv:Header/>
    <soapenv:Body>
        <bt:BTClientes.Obtener>
            <bt:Btinreq>
                <bt:Canal>MIDDLEWARE</bt:Canal>
                <bt:Usuario>USR_MW</bt:Usuario>
                <bt:Token>abc123</bt:Token>
                <bt:Requerimiento>1</bt:Requerimiento>
                <bt:Device>SERVER</bt:Device>
            </bt:Btinreq>
            <bt:clienteUId>12345</bt:clienteUId>
        </bt:BTClientes.Obtener>
    </soapenv:Body>
</soapenv:Envelope>
```

---

## 3. Modulos principales de Bantotal

| Modulo | Operaciones tipicas |
|--------|---------------------|
| **BTClientes** | Crear, Obtener, Actualizar, ListarCuentas, ObtenerDatos |
| **BTCuentas** | ObtenerSaldo, ListarMovimientos, ObtenerDatosCuenta |
| **BTPrestamos** | Simular, Crear, ObtenerCuotas, ListarPagos, CalcularPrepago |
| **BTDepositos** | AbrirPlazoFijo, Renovar, ObtenerCondiciones |
| **BTTransferencias** | Crear, ObtenerEstado, ListarPorFecha |
| **BTOperaciones** | CrearDebito, CrearCredito, Reversar, Confirmar |
| **BTPersonas** | Crear, Obtener, Actualizar, ValidarDocumento |
| **BTMoras** | ObtenerDeudas, CalcularMora, ListarMorosos |

---

## 4. Integracion con Apache Camel

### Ejemplo: Consultar saldo via REST

```java
@Component
public class BantotalRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Error handler para reintentos en caso de timeout
        onException(HttpOperationFailedException.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(5000)
            .handled(true)
            .log(LoggingLevel.ERROR, "Error llamando BTServices: ${exception.message}")
            .setBody(simple("{\"error\": \"Servicio Bantotal no disponible\"}"));

        // Ruta: Consultar saldo de cuenta
        from("rest:post:/api/cuentas/saldo")
            .routeId("consultarSaldoBantotal")
            .log("Consultando saldo para cuenta: ${body}")
            .unmarshal().json(JsonLibrary.Jackson, ConsultaSaldoRequest.class)
            .process(exchange -> {
                ConsultaSaldoRequest req = exchange.getIn().getBody(ConsultaSaldoRequest.class);

                // Construir request para BTServices
                Map<String, Object> btRequest = new HashMap<>();
                btRequest.put("Btinreq", crearCabecera(exchange));
                btRequest.put("cuentaUId", req.getCuentaId());

                exchange.getIn().setBody(btRequest);
            })
            .marshal().json()
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .to("http4://{{bantotal.host}}/BTServices/api/Cuentas/ObtenerSaldo"
                + "?connectTimeout=10000&socketTimeout=30000")
            .unmarshal().json(JsonLibrary.Jackson)
            .process(exchange -> {
                // Mapear respuesta de Bantotal a formato interno
                Map<String, Object> btResponse = exchange.getIn().getBody(Map.class);
                Map<String, Object> btoutreq = (Map) btResponse.get("Btoutreq");

                if (!"OK".equals(btoutreq.get("Estado"))) {
                    throw new BantotalException("Error: " + btResponse.get("Erroresnegocio"));
                }

                SaldoResponse saldo = new SaldoResponse();
                saldo.setSaldoDisponible((Double) btResponse.get("saldoDisponible"));
                saldo.setSaldoContable((Double) btResponse.get("saldoContable"));
                exchange.getIn().setBody(saldo);
            })
            .marshal().json();
    }

    private Map<String, String> crearCabecera(Exchange exchange) {
        Map<String, String> btinreq = new HashMap<>();
        btinreq.put("Canal", "MIDDLEWARE");
        btinreq.put("Usuario", "USR_MW");
        btinreq.put("Token", exchange.getIn().getHeader("bt-token", String.class));
        btinreq.put("Requerimiento", UUID.randomUUID().toString());
        btinreq.put("Device", "CAMEL");
        return btinreq;
    }
}
```

### Ejemplo: Consumir servicio SOAP con CXF

```java
// Ruta para llamar a BTServices SOAP
from("direct:obtenerClienteSoap")
    .routeId("obtenerClienteBantotalSoap")
    .process(exchange -> {
        // Construir request SOAP
        ObtenerClienteRequest request = new ObtenerClienteRequest();
        request.setBtinreq(crearBtinreq());
        request.setClienteUId(exchange.getIn().getHeader("clienteId", Long.class));
        exchange.getIn().setBody(new Object[]{request});
    })
    .to("cxf:bean:btClientesService?dataFormat=POJO&synchronous=true")
    .process(exchange -> {
        // Extraer respuesta SOAP
        MessageContentsList response = exchange.getIn().getBody(MessageContentsList.class);
        ObtenerClienteResponse btResponse = (ObtenerClienteResponse) response.get(0);
        exchange.getIn().setBody(btResponse.getSdtCliente());
    });
```

```xml
<!-- Configuracion del endpoint CXF (Spring XML) -->
<cxf:cxfEndpoint id="btClientesService"
    address="http://${bantotal.host}/BTServices/ws/BTClientes"
    serviceClass="com.bantotal.ws.BTClientes"
    wsdlURL="classpath:wsdl/BTClientes.wsdl">
    <cxf:properties>
        <entry key="dataFormat" value="POJO"/>
    </cxf:properties>
</cxf:cxfEndpoint>
```

---

## 5. Patron tipico de orquestacion con Bantotal

```java
// Caso de uso: Desembolso de credito (orquestacion de multiples servicios BT)
from("jms:queue:solicitudes-desembolso")
    .routeId("orquestacionDesembolso")
    .log("Iniciando desembolso para solicitud: ${body}")

    // Paso 1: Validar cliente
    .enrich("direct:btValidarCliente", enrichStrategy)

    // Paso 2: Verificar linea de credito
    .enrich("direct:btVerificarLinea", enrichStrategy)

    // Paso 3: Simular prestamo
    .to("direct:btSimularPrestamo")

    // Paso 4: Crear prestamo
    .to("direct:btCrearPrestamo")

    // Paso 5: Acreditar monto en cuenta
    .to("direct:btAcreditarCuenta")

    // Paso 6: Notificar al cliente
    .wireTap("jms:queue:notificaciones-credito")

    .log("Desembolso completado: ${body.prestamoId}")
    .to("jms:queue:desembolsos-completados");
```

---

## 6. Manejo de autenticacion BTServices

```java
// Ruta para obtener token de Bantotal
from("direct:btLogin")
    .routeId("bantotalLogin")
    .setBody(simple("{\"Usuario\":\"{{bt.usuario}}\",\"Contrasena\":\"{{bt.password}}\",\"Canal\":\"MIDDLEWARE\"}"))
    .setHeader(Exchange.HTTP_METHOD, constant("POST"))
    .setHeader("Content-Type", constant("application/json"))
    .to("http4://{{bantotal.host}}/BTServices/api/Sesiones/Iniciar")
    .unmarshal().json(JsonLibrary.Jackson)
    .process(exchange -> {
        Map<String, Object> response = exchange.getIn().getBody(Map.class);
        String token = (String) response.get("Token");
        // Cachear token para reutilizar
        exchange.getContext().getGlobalOptions().put("bt.token.actual", token);
    });

// Ruta que renueva token periodicamente
from("timer:renovarToken?period=1800000") // Cada 30 minutos
    .to("direct:btLogin");
```

---

## 7. Errores comunes y manejo

| Codigo | Error | Causa | Solucion |
|--------|-------|-------|----------|
| **100** | Token invalido/expirado | Sesion caducada | Renovar token con Sesiones/Iniciar |
| **200** | Cliente no encontrado | clienteUId invalido | Validar existencia antes de operar |
| **300** | Operacion no permitida | Permisos de canal | Verificar permisos del usuario MW |
| **400** | Saldo insuficiente | Fondos no disponibles | Validar saldo antes de debitar |
| **500** | Error interno BT | Error en core | Reintentar con backoff, escalar si persiste |
| **600** | Servicio no disponible | Mantenimiento | Circuit breaker + fallback |

```java
// Manejo robusto de errores de Bantotal
.process(exchange -> {
    Map<String, Object> response = exchange.getIn().getBody(Map.class);
    List<Map<String, Object>> errores = (List) response.get("Erroresnegocio");

    if (errores != null && !errores.isEmpty()) {
        String codigo = String.valueOf(errores.get(0).get("Codigo"));
        String descripcion = (String) errores.get(0).get("Descripcion");

        switch (codigo) {
            case "100":
                throw new BantotalTokenException("Token expirado");
            case "400":
                throw new SaldoInsuficienteException(descripcion);
            default:
                throw new BantotalNegocioException(codigo, descripcion);
        }
    }
})
```

---

## Preguntas de entrevista

### Basicas
1. **Que es Bantotal?**
   - Core bancario que gestiona las operaciones centrales de una entidad financiera (cuentas, creditos, clientes, transacciones)
2. **Que es BTServices?**
   - Capa de servicios (API REST y SOAP) que expone las operaciones del core Bantotal para integracion con sistemas externos
3. **Que es el Btinreq?**
   - Cabecera obligatoria en toda peticion: contiene Canal, Usuario, Token, Device, Requerimiento
4. **BTServices soporta REST y SOAP?**
   - Si. REST es mas moderno y preferido para nuevos desarrollos. SOAP se mantiene por compatibilidad con sistemas legacy
5. **Cuales son los modulos principales de Bantotal?**
   - BTClientes, BTCuentas, BTPrestamos, BTTransferencias, BTOperaciones, BTDepositos

### Intermedias
6. **Como manejas la autenticacion con BTServices?**
   - Sesiones/Iniciar para obtener token, renovacion periodica, cacheo del token, manejo de expiracion
7. **Como orquestas una operacion que involucra multiples llamadas a BT?**
   - Ruta Camel con pasos secuenciales, enrichment, manejo de errores en cada paso, compensacion si falla
8. **Que diferencia hay entre usar REST y SOAP con Bantotal?**
   - REST: JSON, mas simple, camel-http. SOAP: XML/WSDL, WS-Security, camel-cxf, necesario para operaciones legacy
9. **Como manejas errores de negocio de Bantotal?**
   - Evaluar campo "Erroresnegocio" en la respuesta, mapear codigos a excepciones especificas
10. **Que es un cuentaUId / clienteUId?**
    - Identificador unico interno de Bantotal para cuentas y clientes. Se usa en todas las operaciones

### Avanzadas
11. **Como implementas compensacion si falla un paso en la orquestacion?**
    - Saga pattern: cada paso tiene su compensacion (ej: si falla acreditar, reversar el prestamo creado)
12. **Como garantizas idempotencia en operaciones financieras con BT?**
    - Usar ID unico de requerimiento, IdempotentConsumer en Camel, validar estado antes de operar
13. **Como manejas la concurrencia en operaciones sobre la misma cuenta?**
    - Bantotal maneja bloqueo interno, pero el middleware debe serializar requests con la misma cuentaUId

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| Que es Bantotal? | Core bancario para bancos y microfinancieras de LATAM |
| Que es BTServices? | APIs REST/SOAP para integrar con el core |
| Que es Btinreq? | Cabecera obligatoria (Canal, Usuario, Token) |
| REST o SOAP? | REST preferido, SOAP para legacy |
| Modulos clave? | BTClientes, BTCuentas, BTPrestamos, BTTransferencias |
| Como autenticarse? | Sesiones/Iniciar → Token → cachear y renovar |
| Errores de negocio? | Campo "Erroresnegocio" en respuesta con codigo y descripcion |
| Orquestacion? | Camel route con pasos secuenciales + enrichment + compensacion |
| Idempotencia? | ID unico de requerimiento + IdempotentConsumer |

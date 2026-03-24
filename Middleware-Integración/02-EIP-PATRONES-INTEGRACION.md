# Patrones de Integracion Empresarial (EIP)

---

## 1. Que son los EIP?

Los **Enterprise Integration Patterns** son patrones de diseno para integracion de sistemas, definidos por Gregor Hohpe y Bobby Woolf. Apache Camel los implementa como DSL.

```
Sistema A ──→ [Canal] ──→ [Router] ──→ [Transformador] ──→ [Canal] ──→ Sistema B
```

---

## 2. Patrones de Mensajeria (Messaging Patterns)

### Message Channel
Canal por donde viajan los mensajes. En Camel = endpoints (JMS queue, Kafka topic, direct, seda).

```java
from("jms:queue:entrada")     // Point-to-Point Channel (un solo consumidor)
    .to("jms:topic:eventos");  // Publish-Subscribe Channel (multiples consumidores)
```

### Message (Mensaje)
Unidad de datos que viaja por el canal. En Camel = Exchange con headers + body.

| Parte | Descripcion |
|-------|-------------|
| **Header** | Metadatos (correlationId, tipo operacion, timestamp) |
| **Body** | Contenido del mensaje (JSON, XML, objeto Java) |
| **Properties** | Datos internos de la ruta (no se propagan entre endpoints) |

### Pipes and Filters
Encadenar procesadores donde cada uno realiza una operacion sobre el mensaje.

```java
from("jms:queue:transacciones")
    .filter(simple("${body.monto} > 0"))        // Filter
    .bean(validadorService)                       // Pipe
    .bean(transformadorService)                   // Pipe
    .bean(persistenciaService)                    // Pipe
    .to("jms:queue:transacciones-procesadas");
```

---

## 3. Patrones de Ruteo (Routing Patterns)

### Content-Based Router
Rutea el mensaje segun su contenido a diferentes destinos.

```java
// Ejemplo: Rutear operaciones bancarias segun tipo
from("jms:queue:operaciones-bancarias")
    .choice()
        .when(simple("${body.tipoOperacion} == 'TRANSFERENCIA'"))
            .to("direct:procesarTransferencia")
        .when(simple("${body.tipoOperacion} == 'PAGO_SERVICIO'"))
            .to("direct:procesarPagoServicio")
        .when(simple("${body.tipoOperacion} == 'CONSULTA_SALDO'"))
            .to("direct:consultarSaldo")
        .otherwise()
            .to("direct:operacionDesconocida")
    .end();
```

### Message Filter
Descarta mensajes que no cumplen un criterio.

```java
from("kafka:transacciones")
    .filter(simple("${body.estado} == 'PENDIENTE'"))
    .to("direct:procesarPendientes");

// Con Predicate custom
.filter(exchange -> {
    Transaccion tx = exchange.getIn().getBody(Transaccion.class);
    return tx.getMonto().compareTo(BigDecimal.ZERO) > 0;
})
```

### Recipient List
Enviar un mensaje a multiples destinos dinamicos.

```java
// Notificar a multiples canales segun tipo de cliente
from("direct:notificar")
    .recipientList(simple("direct:${header.canal1},direct:${header.canal2}"))
    .parallelProcessing();  // Enviar en paralelo
```

### Routing Slip
Ruta dinamica definida en el header del mensaje (secuencia de endpoints).

```java
from("direct:entrada")
    .routingSlip(header("rutaProcesamiento"));
// El header contiene: "direct:validar,direct:enriquecer,direct:persistir"
```

### Dynamic Router
Router que decide el siguiente destino en cada paso.

```java
from("direct:inicio")
    .dynamicRouter(method(routerBean, "determinarSiguientePaso"));

// El bean retorna null cuando termina la ruta
```

---

## 4. Patrones de Transformacion

### Message Translator
Convertir mensaje de un formato a otro.

```java
// De formato interno a formato Bantotal
from("direct:enviarABantotal")
    .process(exchange -> {
        CreditoInterno credito = exchange.getIn().getBody(CreditoInterno.class);
        BantotalRequest btReq = new BantotalRequest();
        btReq.setBtinreq(crearCabecera());
        btReq.setSdtPrestamo(mapearPrestamo(credito));
        exchange.getIn().setBody(btReq);
    })
    .marshal().json()
    .to("http4://bantotal/api/Prestamos/Simular");
```

### Content Enricher
Enriquecer el mensaje con datos de una fuente externa.

```java
from("direct:solicitudCredito")
    // Enrich: consultar score crediticio y agregar al mensaje
    .enrich("direct:consultarScore", (original, scorResponse) -> {
        SolicitudCredito solicitud = original.getIn().getBody(SolicitudCredito.class);
        ScoreResponse score = scorResponse.getIn().getBody(ScoreResponse.class);
        solicitud.setScoreCrediticio(score.getScore());
        solicitud.setNivelRiesgo(score.getNivelRiesgo());
        original.getIn().setBody(solicitud);
        return original;
    })
    .to("direct:evaluar");

// PollEnrich: enriquecer con datos de un recurso que requiere polling
from("direct:generarReporte")
    .pollEnrich("file:/datos/reportes?fileName=reporte-diario.csv", 5000)
    .to("direct:procesarReporte");
```

### Claim Check
Guardar datos grandes temporalmente y recuperarlos despues.

```java
from("direct:operacionPesada")
    .claimCheck(ClaimCheckOperation.Push)  // Guardar body en memoria
    .transform(simple("${body.id}"))       // Trabajar solo con ID
    .to("direct:procesoLigero")
    .claimCheck(ClaimCheckOperation.Pop)   // Recuperar body completo
    .to("direct:siguientePaso");
```

---

## 5. Patrones de Division y Agregacion

### Splitter
Dividir un mensaje en partes y procesar cada una individualmente.

```java
// Procesar lista de pagos individualmente
from("jms:queue:lote-pagos")
    .unmarshal().json(JsonLibrary.Jackson, LotePagos.class)
    .split(simple("${body.pagos}"))    // Dividir la lista
        .parallelProcessing()           // Procesar en paralelo
        .streaming()                    // No cargar todo en memoria
        .bean(pagoService, "procesar")
    .end()
    .log("Lote procesado completamente");
```

### Aggregator
Combinar multiples mensajes en uno solo.

```java
from("direct:respuestasParciales")
    .aggregate(header("correlationId"), new MiEstrategiaAgregacion())
        .completionSize(3)              // Agregar cuando lleguen 3
        .completionTimeout(30000)       // O timeout de 30 segundos
    .to("direct:respuestaConsolidada");

// Estrategia de agregacion
public class MiEstrategiaAgregacion implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) return newExchange;

        List<Object> resultados = oldExchange.getIn().getBody(List.class);
        resultados.add(newExchange.getIn().getBody());
        oldExchange.getIn().setBody(resultados);
        return oldExchange;
    }
}
```

### Scatter-Gather
Enviar request a multiples destinos, recolectar respuestas y consolidar. Combinacion de Recipient List + Aggregator.

```java
// Consultar score en multiples centrales de riesgo y consolidar
from("direct:consultarRiesgo")
    .multicast(new MejorScoreStrategy())
        .parallelProcessing()
        .to("direct:centralRiesgoA", "direct:centralRiesgoB", "direct:centralRiesgoC")
    .end()
    .to("direct:evaluarCredito");
```

---

## 6. Patrones de Control

### Wire Tap
Enviar copia del mensaje a otro destino sin afectar el flujo principal (auditoria, logging).

```java
from("direct:transferencia")
    .wireTap("jms:queue:auditoria-transacciones")  // Copia para auditoria
    .to("direct:ejecutarTransferencia");            // Flujo principal continua
```

### Throttler
Controlar el rate de mensajes procesados.

```java
from("jms:queue:solicitudes")
    .throttle(100)              // Maximo 100 mensajes
    .timePeriodMillis(60000)    // Por minuto
    .to("direct:procesar");
```

### Circuit Breaker
Proteger contra fallos en servicios externos.

```java
from("direct:llamarServicioExterno")
    .circuitBreaker()
        .resilience4jConfiguration()
            .failureRateThreshold(50)
            .waitDurationInOpenState(30)
            .slidingWindowSize(10)
        .end()
        .to("http4://servicio-externo/api/operacion")
    .onFallback()
        .setBody(constant("{\"error\": \"Servicio no disponible\"}"))
    .end();
```

### Idempotent Consumer
Evitar procesar mensajes duplicados.

```java
// Con repositorio JDBC (persistente entre reinicios)
from("jms:queue:pagos")
    .idempotentConsumer(
        header("idTransaccion"),
        new JdbcMessageIdRepository(dataSource, "PROCESSED_MESSAGES")
    )
    .to("direct:procesarPago");
```

---

## 7. Resequencer
Reordenar mensajes que llegan desordenados.

```java
from("jms:queue:eventos")
    .resequence(header("sequenceNumber"))
        .batch()
        .size(100)
        .timeout(5000)
    .to("direct:procesarOrdenado");
```

---

## Preguntas de entrevista

### Basicas
1. **Que son los EIP y por que son importantes?**
   - Patrones probados para integracion de sistemas. Definen soluciones reutilizables a problemas comunes de comunicacion entre aplicaciones
2. **Diferencia entre Point-to-Point y Publish-Subscribe?**
   - P2P: un mensaje lo consume un solo consumidor (queue). Pub-Sub: un mensaje lo reciben todos los suscriptores (topic)
3. **Que es el Content-Based Router?**
   - Rutea mensajes a diferentes destinos segun el contenido del mensaje (.choice().when())
4. **Que es el Splitter y cuando lo usas?**
   - Divide un mensaje en partes para procesarlas individualmente. Ejemplo: procesar cada pago de un lote
5. **Que es Wire Tap?**
   - Envia copia del mensaje a otro destino (auditoria) sin afectar el flujo principal

### Intermedias
6. **Como combinas Splitter con Aggregator?**
   - Split para procesar items individuales, luego Aggregate para reconsolidar resultados. Usar correlationId para agrupar
7. **Diferencia entre enrich y pollEnrich?**
   - enrich: fuente bajo demanda (HTTP, direct). pollEnrich: fuente tipo polling (file, FTP, JMS)
8. **Cuando usas Routing Slip vs Dynamic Router?**
   - Routing Slip: ruta predefinida en header. Dynamic Router: decision paso a paso con logica custom
9. **Como implementas Scatter-Gather?**
   - Multicast a multiples destinos + AggregationStrategy para consolidar respuestas

### Avanzadas
10. **Como garantizas idempotencia en un sistema financiero?**
    - IdempotentConsumer con repositorio persistente (JDBC), usar ID unico de transaccion como clave
11. **Como manejas backpressure con Throttler?**
    - throttle(N).timePeriodMillis(T) para limitar rate, combinar con SEDA queue como buffer

---

## Tabla de respuestas rapidas

| Patron | Que hace | DSL en Camel |
|--------|----------|--------------|
| Content-Based Router | Rutea segun contenido | `.choice().when().to()` |
| Message Filter | Descarta mensajes | `.filter(predicate)` |
| Splitter | Divide mensaje | `.split(expression)` |
| Aggregator | Combina mensajes | `.aggregate(correlation, strategy)` |
| Content Enricher | Agrega datos externos | `.enrich(uri, strategy)` |
| Wire Tap | Copia para auditoria | `.wireTap(uri)` |
| Recipient List | Multiples destinos | `.recipientList(expression)` |
| Throttler | Limita rate | `.throttle(N)` |
| Idempotent Consumer | Evita duplicados | `.idempotentConsumer(key, repo)` |
| Circuit Breaker | Protege ante fallos | `.circuitBreaker()` |
| Dead Letter Channel | Cola de errores | `errorHandler(deadLetterChannel(uri))` |
| Resequencer | Reordena mensajes | `.resequence(expression)` |

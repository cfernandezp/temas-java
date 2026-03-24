# Red Hat AMQ y Mensajeria

---

## 1. Que es Red Hat AMQ?

**Red Hat AMQ** es la plataforma de mensajeria empresarial de Red Hat basada en:
- **AMQ Broker** = Apache ActiveMQ Artemis (mensaje broker)
- **AMQ Streams** = Apache Kafka (event streaming)
- **AMQ Interconnect** = Apache Qpid Dispatch Router (AMQP router)

```
[Productor]                      [Consumidor]
    │                                 ▲
    │    ┌──────────────────────┐      │
    └──→ │   AMQ Broker         │ ─────┘
         │  (ActiveMQ Artemis)  │
         │                      │
         │  Queue: solicitudes  │  ← Point-to-Point (un consumidor)
         │  Topic: eventos      │  ← Pub-Sub (multiples consumidores)
         └──────────────────────┘
```

---

## 2. Conceptos de mensajeria

| Concepto | Descripcion |
|----------|-------------|
| **Queue** | Cola punto-a-punto: un mensaje lo consume UN solo consumidor |
| **Topic** | Publicar-suscribir: un mensaje lo reciben TODOS los suscriptores |
| **JMS** | Java Message Service: API estandar Java para mensajeria |
| **AMQP** | Advanced Message Queuing Protocol: protocolo abierto de mensajeria |
| **Broker** | Servidor que recibe, almacena y entrega mensajes |
| **Producer** | Envia mensajes al broker |
| **Consumer** | Recibe mensajes del broker |
| **DLQ (Dead Letter Queue)** | Cola donde van mensajes que no pudieron procesarse |
| **Message Selector** | Filtro para que el consumidor reciba solo ciertos mensajes |
| **Durable Subscription** | Suscripcion a topic que persiste mensajes si el consumidor se desconecta |
| **Acknowledgement** | Confirmacion de que el mensaje fue procesado correctamente |
| **Transaction** | Agrupar envios/recepciones en una unidad atomica |

---

## 3. Modos de acknowledgement

| Modo | Descripcion | Cuando usar |
|------|-------------|-------------|
| **AUTO_ACKNOWLEDGE** | Ack automatico al recibir | Operaciones no criticas |
| **CLIENT_ACKNOWLEDGE** | Ack manual con `message.acknowledge()` | Control total |
| **SESSION_TRANSACTED** | Ack con `session.commit()` / `session.rollback()` | Operaciones financieras |
| **DUPS_OK_ACKNOWLEDGE** | Ack lazy, puede haber duplicados | Alto throughput, tolerante a duplicados |

---

## 4. JMS con Spring Boot y Camel

### Configuracion

```yaml
# application.yml
spring:
  activemq:
    broker-url: tcp://amq-broker:61616
    user: admin
    password: ${AMQ_PASSWORD}
    pool:
      enabled: true
      max-connections: 10

camel:
  component:
    jms:
      connection-factory: "#pooledConnectionFactory"
      transacted: true
      acknowledgement-mode-name: SESSION_TRANSACTED
```

```java
// Configuracion del ConnectionFactory
@Configuration
public class JmsConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://amq-broker:61616");
        factory.setUser("admin");
        factory.setPassword("secret");
        return factory;
    }

    @Bean
    public ConnectionFactory pooledConnectionFactory(ConnectionFactory connectionFactory) {
        PooledConnectionFactory pooled = new PooledConnectionFactory();
        pooled.setConnectionFactory(connectionFactory);
        pooled.setMaxConnections(10);
        return pooled;
    }
}
```

### Productor JMS

```java
// Con Spring JmsTemplate
@Service
public class TransaccionProducer {

    private final JmsTemplate jmsTemplate;

    public void enviarTransaccion(Transaccion tx) {
        jmsTemplate.convertAndSend("queue.transacciones", tx, message -> {
            message.setStringProperty("tipoOperacion", tx.getTipo());
            message.setJMSCorrelationID(tx.getIdTransaccion());
            message.setJMSPriority(tx.esCritica() ? 9 : 4);
            return message;
        });
    }
}

// Con Camel
from("direct:enviarTransaccion")
    .setHeader("tipoOperacion", simple("${body.tipo}"))
    .setHeader(JmsConstants.JMS_DELIVERY_MODE, constant(2))  // PERSISTENT
    .to("jms:queue:transacciones?deliveryPersistent=true");
```

### Consumidor JMS

```java
// Con Spring @JmsListener
@Component
public class TransaccionConsumer {

    @JmsListener(destination = "queue.transacciones",
                 containerFactory = "jmsListenerContainerFactory",
                 selector = "tipoOperacion = 'TRANSFERENCIA'")
    public void procesarTransferencia(Transaccion tx, Session session) {
        try {
            transferService.ejecutar(tx);
            session.commit();   // Confirmar procesamiento
        } catch (Exception e) {
            session.rollback(); // Devolver mensaje a la cola
            throw e;
        }
    }
}

// Con Camel
from("jms:queue:transacciones?concurrentConsumers=5&maxConcurrentConsumers=10")
    .routeId("procesarTransacciones")
    .transacted()           // Transaccion JMS
    .bean(transaccionService, "procesar")
    .log("Transaccion procesada: ${header.JMSCorrelationID}");
```

---

## 5. Patrones de mensajeria

### Request-Reply (sincrono sobre JMS)

```java
// PRODUCTOR: envia y espera respuesta
from("rest:post:/api/saldo")
    .to("jms:queue:consulta-saldo?exchangePattern=InOut"
        + "&replyTo=queue.respuesta-saldo"
        + "&requestTimeout=30000")   // Espera max 30 seg
    .log("Saldo recibido: ${body}");

// CONSUMIDOR: procesa y responde
from("jms:queue:consulta-saldo")
    .bean(saldoService, "consultar")
    .log("Respondiendo saldo: ${body}");
    // Camel automaticamente responde a la replyTo queue
```

### Dead Letter Queue

```java
// Configurar DLQ en Camel
errorHandler(deadLetterChannel("jms:queue:DLQ.transacciones")
    .maximumRedeliveries(5)
    .redeliveryDelay(5000)
    .useExponentialBackOff()
    .backOffMultiplier(2));

// Procesador de DLQ (alertas, reintentos manuales)
from("jms:queue:DLQ.transacciones")
    .routeId("procesarDLQ")
    .log(LoggingLevel.ERROR, "Mensaje en DLQ: ${body}")
    .bean(alertaService, "notificarErrorTransaccion")
    .to("jms:queue:transacciones-fallidas-auditoria");
```

### Competing Consumers (escalabilidad)

```java
// Multiples consumidores compiten por mensajes de la misma cola
from("jms:queue:pagos?concurrentConsumers=5&maxConcurrentConsumers=20")
    .routeId("procesarPagos")
    .bean(pagoService, "procesar");
// 5 threads iniciales, escala hasta 20 segun carga
```

---

## 6. AMQ Broker - Configuracion (broker.xml)

```xml
<configuration xmlns="urn:activemq:core">
    <!-- Acceptors: protocolos que acepta el broker -->
    <acceptors>
        <acceptor name="artemis">tcp://0.0.0.0:61616?protocols=CORE,AMQP,OPENWIRE</acceptor>
        <acceptor name="amqp">tcp://0.0.0.0:5672?protocols=AMQP</acceptor>
    </acceptors>

    <!-- Direcciones y colas -->
    <addresses>
        <address name="transacciones">
            <anycast>
                <queue name="transacciones" max-consumers="20">
                    <durable>true</durable>
                </queue>
            </anycast>
        </address>

        <address name="eventos.transacciones">
            <multicast>  <!-- Topic -->
                <queue name="eventos.auditoria" durable="true"/>
                <queue name="eventos.notificaciones" durable="true"/>
            </multicast>
        </address>
    </addresses>

    <!-- Politicas de direccion -->
    <address-settings>
        <address-setting match="#">
            <dead-letter-address>DLQ</dead-letter-address>
            <expiry-address>ExpiryQueue</expiry-address>
            <max-delivery-attempts>5</max-delivery-attempts>
            <redelivery-delay>10000</redelivery-delay>
            <max-size-bytes>100MB</max-size-bytes>
            <message-counter-history-day-limit>7</message-counter-history-day-limit>
        </address-setting>
    </address-settings>

    <!-- Persistencia -->
    <store>
        <journal directory="data/journal" min-files="2" pool-buffers="true"/>
    </store>
</configuration>
```

---

## 7. AMQ en OpenShift

```yaml
# Operador AMQ - Crear broker
apiVersion: broker.amq.io/v1beta1
kind: ActiveMQArtemis
metadata:
  name: amq-broker
  namespace: middleware-prod
spec:
  deploymentPlan:
    size: 2                    # 2 replicas (HA)
    image: registry.redhat.io/amq7/amq-broker-rhel8:latest
    requireLogin: true
    persistenceEnabled: true
    messageMigration: true     # Migrar mensajes si un pod muere
    resources:
      requests:
        cpu: "500m"
        memory: "1Gi"
      limits:
        cpu: "1"
        memory: "2Gi"
  acceptors:
    - name: amqp
      protocols: amqp
      port: 5672
    - name: all
      protocols: all
      port: 61616
  console:
    expose: true               # Exponer consola web
```

---

## 8. Mensajeria sincronica vs asincronica

| Aspecto | Sincronica | Asincronica |
|---------|-----------|-------------|
| **Patron** | Request-Reply | Fire-and-Forget |
| **Espera** | Productor espera respuesta | Productor no espera |
| **Acoplamiento** | Temporal (ambos deben estar activos) | Desacoplado (broker almacena) |
| **Latencia** | Mayor (espera respuesta) | Menor (solo envio) |
| **Uso tipico** | Consulta de saldo, validacion | Notificaciones, eventos, batch |
| **En Camel** | `exchangePattern=InOut` + `replyTo` | `exchangePattern=InOnly` |

```java
// SINCRONO: consultar saldo (necesito la respuesta)
from("rest:post:/api/saldo")
    .to("jms:queue:consulta-saldo?exchangePattern=InOut&requestTimeout=30000");

// ASINCRONO: registrar evento (no necesito respuesta)
from("direct:registrarEvento")
    .to("jms:queue:eventos-auditoria?exchangePattern=InOnly");
```

---

## Preguntas de entrevista

### Basicas
1. **Que es Red Hat AMQ?**
   - Plataforma de mensajeria empresarial basada en ActiveMQ Artemis (broker) y Kafka (streaming)
2. **Diferencia entre Queue y Topic?**
   - Queue: punto-a-punto (un consumidor). Topic: pub-sub (todos los suscriptores)
3. **Que es JMS?**
   - Java Message Service: API estandar de Java para enviar/recibir mensajes de forma asincrona
4. **Que es una Dead Letter Queue?**
   - Cola donde van mensajes que fallan despues de N reintentos, para revision y reprocesamiento
5. **Que modos de acknowledgement existen?**
   - AUTO_ACKNOWLEDGE, CLIENT_ACKNOWLEDGE, SESSION_TRANSACTED, DUPS_OK_ACKNOWLEDGE

### Intermedias
6. **Como implementas request-reply con JMS?**
   - Usar replyTo queue, correlationId para matching, timeout para evitar espera indefinida
7. **Como garantizas que un mensaje no se pierda?**
   - Mensajes persistentes (deliveryPersistent=true), ack transaccional, DLQ para fallos
8. **Diferencia entre mensajeria sincronica y asincronica?**
   - Sincrona: request-reply, productor espera respuesta. Asincrona: fire-and-forget, desacoplado
9. **Como escalas consumidores?**
   - concurrentConsumers/maxConcurrentConsumers en Camel, competing consumers pattern
10. **Diferencia entre ActiveMQ classic y Artemis?**
    - Artemis: no-blocking I/O, mejor rendimiento, journal persistente, AMQP nativo. Es el futuro

### Avanzadas
11. **Como manejas orden de mensajes con multiples consumidores?**
    - Message groups: mensajes con mismo JMSXGroupID van al mismo consumidor
12. **Como manejas mensajeria transaccional con base de datos?**
    - XA Transaction (2PC) con JTA, o Transactional Outbox pattern
13. **Como configuras AMQ Broker en HA?**
    - Live-backup pairs, shared store o replication, message migration en OpenShift

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| Que es AMQ? | Plataforma mensajeria Red Hat (Artemis + Kafka) |
| Queue vs Topic? | Queue=1 consumidor, Topic=todos los suscriptores |
| JMS? | API estandar Java para mensajeria |
| DLQ? | Cola para mensajes que fallan tras N reintentos |
| Sincrono vs asincrono? | Sincrono=espera respuesta, asincrono=fire-and-forget |
| Persistencia? | deliveryPersistent=true + journal en disco |
| Escalabilidad? | Competing consumers + concurrentConsumers |
| Request-Reply? | replyTo queue + correlationId + timeout |
| AMQP? | Protocolo abierto de mensajeria (alternativa a OpenWire) |
| HA? | Live-backup pairs con shared store o replication |

# Kafka y Mensajeria

---

## 1. Que es Apache Kafka?

Sistema de **mensajeria distribuida** para comunicacion asincrona entre microservicios. Funciona como un "log" de eventos ordenados y persistentes.

```
Productor → [Topic: "pedidos"] → Consumidor
              Particion 0: msg1, msg3, msg5
              Particion 1: msg2, msg4, msg6
```

| Concepto | Que es |
|----------|--------|
| **Topic** | Canal/categoria de mensajes (ej: "pedidos", "notificaciones") |
| **Particion** | Subdivision del topic para paralelismo. Cada particion mantiene orden |
| **Producer** | Envia mensajes a un topic |
| **Consumer** | Lee mensajes de un topic |
| **Consumer Group** | Grupo de consumers que se reparten las particiones (escalabilidad) |
| **Broker** | Servidor Kafka que almacena los mensajes |
| **Offset** | Posicion del mensaje en la particion (como un indice) |
| **Zookeeper/KRaft** | Coordina el cluster de brokers (KRaft reemplaza Zookeeper desde Kafka 3.x) |

---

## 2. Producer vs Consumer

```java
// PRODUCER: envia mensajes al topic
@Service
public class PedidoProducer {

    private final KafkaTemplate<String, PedidoEvent> kafkaTemplate;

    public void enviarPedido(PedidoEvent evento) {
        kafkaTemplate.send("pedidos", evento.getPedidoId(), evento);
        // send(topic, key, mensaje)
        // key determina a que particion va (misma key = misma particion = orden garantizado)
    }
}

// CONSUMER: lee mensajes del topic
@Service
public class PedidoConsumer {

    @KafkaListener(topics = "pedidos", groupId = "servicio-inventario")
    public void procesarPedido(PedidoEvent evento) {
        // Este metodo se ejecuta automaticamente cuando llega un mensaje
        inventarioService.reservarStock(evento.getProductoId(), evento.getCantidad());
    }
}
```

---

## 3. Configuracion Spring Boot + Kafka

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: mi-servicio
      auto-offset-reset: earliest   # leer desde el inicio si es consumer nuevo
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.miapp.eventos"
```

```java
// Configuracion programatica (crear topics)
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic pedidosTopic() {
        return TopicBuilder.name("pedidos")
            .partitions(3)        // 3 particiones para paralelismo
            .replicas(2)          // 2 copias para tolerancia a fallos
            .build();
    }
}
```

---

## 4. Consumer Groups y escalabilidad

```
Topic "pedidos" con 3 particiones:

Consumer Group "servicio-inventario" (3 instancias):
  Consumer A → Particion 0
  Consumer B → Particion 1
  Consumer C → Particion 2
  → Cada consumer procesa 1/3 de los mensajes (paralelismo)

Consumer Group "servicio-notificaciones" (1 instancia):
  Consumer X → Particiones 0, 1, 2
  → Un solo consumer procesa TODOS los mensajes

REGLA: cada particion solo puede ser leida por UN consumer del mismo group.
Si tienes mas consumers que particiones, los extras quedan idle.
```

> **Tip entrevista:** "Los Consumer Groups permiten escalar el procesamiento. Si tengo 3 particiones, puedo tener hasta 3 consumers en el mismo grupo procesando en paralelo. Cada grupo recibe todos los mensajes, lo que permite que inventario y notificaciones procesen los mismos eventos independientemente."

---

## 5. Garantias de entrega

| Garantia | Que significa | Cuando usar |
|----------|--------------|-------------|
| **At most once** | Mensaje puede perderse, nunca duplicarse | Metricas, logs no criticos |
| **At least once** | Mensaje nunca se pierde, puede duplicarse | Pedidos (con idempotencia) |
| **Exactly once** | Ni se pierde ni se duplica | Transacciones financieras |

```java
// At least once (por defecto) + idempotencia en consumer
@KafkaListener(topics = "pagos", groupId = "servicio-pagos")
public void procesarPago(PagoEvent evento) {
    // Verificar si ya se proceso (idempotencia)
    if (pagoRepository.existsByTransaccionId(evento.getTransaccionId())) {
        log.info("Pago ya procesado, ignorando duplicado: {}", evento.getTransaccionId());
        return;
    }
    pagoService.procesar(evento);
}

// Configurar producer para exactly-once (transaccional)
spring:
  kafka:
    producer:
      transaction-id-prefix: tx-   # habilita transacciones
```

---

## 6. Kafka vs otros sistemas de mensajeria

| | Kafka | RabbitMQ | AWS SQS |
|---|---|---|---|
| Modelo | Log de eventos (pull) | Cola de mensajes (push) | Cola de mensajes (pull) |
| Persistencia | Mensajes persisten en disco | Se borran al consumir | Se borran al consumir |
| Orden | Garantizado por particion | No garantizado (por defecto) | FIFO opcional |
| Throughput | Muy alto (millones msg/seg) | Medio | Medio |
| Caso de uso | Event streaming, microservicios | Tareas asincronas, RPC | Cloud-native, serverless |

---

## 7. Patron Event-Driven con Kafka

```java
// Ejemplo: Pedido creado → multiples servicios reaccionan

// Servicio de Pedidos (PRODUCER)
@Service
public class PedidoService {

    public PedidoDTO crearPedido(PedidoRequest request) {
        Pedido pedido = pedidoRepository.save(mapToEntity(request));

        // Publicar evento (los consumidores reaccionan)
        kafkaTemplate.send("pedidos.creados", new PedidoCreado(
            pedido.getId(),
            pedido.getProductoId(),
            pedido.getCantidad(),
            pedido.getUsuarioId()
        ));

        return mapToDTO(pedido);
    }
}

// Servicio de Inventario (CONSUMER)
@KafkaListener(topics = "pedidos.creados", groupId = "inventario")
public void reservarStock(PedidoCreado evento) {
    inventarioService.reservar(evento.getProductoId(), evento.getCantidad());
}

// Servicio de Notificaciones (CONSUMER - otro grupo, recibe los mismos mensajes)
@KafkaListener(topics = "pedidos.creados", groupId = "notificaciones")
public void notificarUsuario(PedidoCreado evento) {
    emailService.enviar(evento.getUsuarioId(), "Tu pedido fue creado");
}
```

---

## 8. Dead Letter Topic (DLT)

```java
// Cuando un mensaje falla multiples veces, se envia a un topic de "mensajes muertos"
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
        DeadLetterPublishingRecoverer recoverer =
            new DeadLetterPublishingRecoverer(template);

        // Reintentar 3 veces, luego enviar a DLT
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
        // Esperar 1 segundo entre reintentos, maximo 3 intentos
    }
}

// El mensaje fallido se envia automaticamente al topic: "pedidos.creados.DLT"
// Un proceso separado puede revisar y reprocesar estos mensajes
```

---

## RESPUESTAS RAPIDAS - KAFKA

| Pregunta | Respuesta |
|----------|-----------|
| Que es Kafka? | Sistema de mensajeria distribuida para comunicacion asincrona entre microservicios |
| Producer vs Consumer? | Producer envia mensajes a un topic. Consumer los lee y procesa |
| Que es un Topic? | Canal de mensajes categorizado (ej: "pedidos"). Tiene particiones para paralelismo |
| Que es Consumer Group? | Grupo de consumers que se reparten las particiones. Permite escalar el procesamiento |
| Kafka vs RabbitMQ? | Kafka persiste mensajes y tiene mayor throughput. RabbitMQ es mas simple para colas |
| Como garantizar orden? | Misma key = misma particion = orden garantizado |
| Que es un offset? | Posicion del mensaje en la particion. El consumer controla hasta donde leyo |
| Dead Letter Topic? | Topic donde van los mensajes que fallaron multiples veces, para revision manual |

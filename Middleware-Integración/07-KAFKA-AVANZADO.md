# Apache Kafka Avanzado

---

## 1. Kafka en el contexto financiero

Kafka se usa en microfinanzas para **event streaming**: capturar y reaccionar a eventos en tiempo real (transacciones, cambios de estado, notificaciones).

```
[Core Bantotal] ──→ [Kafka Topic: transacciones] ──→ [Antifraude]
                                                  ──→ [Notificaciones]
                                                  ──→ [Contabilidad]
                                                  ──→ [Auditoria]
```

---

## 2. AMQ Streams (Kafka en OpenShift)

**Red Hat AMQ Streams** = Apache Kafka con operador para OpenShift/Kubernetes.

```yaml
# Strimzi/AMQ Streams - Crear cluster Kafka
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: banco-kafka
  namespace: middleware-prod
spec:
  kafka:
    version: 3.6.0
    replicas: 3
    listeners:
      - name: plain
        port: 9092
        type: internal
        tls: false
      - name: tls
        port: 9093
        type: internal
        tls: true
    storage:
      type: persistent-claim
      size: 50Gi
      class: managed-premium
    config:
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
      default.replication.factor: 3
      min.insync.replicas: 2
      log.retention.hours: 168       # 7 dias
  zookeeper:
    replicas: 3
    storage:
      type: persistent-claim
      size: 10Gi
  entityOperator:
    topicOperator: {}
    userOperator: {}
```

```yaml
# Crear topic
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaTopic
metadata:
  name: transacciones
  labels:
    strimzi.io/cluster: banco-kafka
spec:
  partitions: 12
  replicas: 3
  config:
    retention.ms: 604800000        # 7 dias
    cleanup.policy: delete
    min.insync.replicas: 2
```

---

## 3. Kafka Connect

Plataforma para mover datos entre Kafka y sistemas externos sin escribir codigo.

```
[Base de datos] ←──Source Connector──→ [Kafka] ←──Sink Connector──→ [Elasticsearch]
```

| Conector | Tipo | Uso |
|----------|------|-----|
| **Debezium** | Source | CDC (Change Data Capture) desde BD |
| **JDBC Source** | Source | Polling de tablas con queries |
| **JDBC Sink** | Sink | Escribir en BD desde topics |
| **Elasticsearch Sink** | Sink | Indexar en Elasticsearch |
| **File Sink/Source** | Ambos | Leer/escribir archivos |
| **S3 Sink** | Sink | Archivado en S3 |

```yaml
# KafkaConnect en OpenShift
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaConnect
metadata:
  name: banco-connect
  annotations:
    strimzi.io/use-connector-resources: "true"
spec:
  version: 3.6.0
  replicas: 2
  bootstrapServers: banco-kafka-kafka-bootstrap:9092
  config:
    group.id: banco-connect-cluster
    key.converter: org.apache.kafka.connect.json.JsonConverter
    value.converter: org.apache.kafka.connect.json.JsonConverter
  build:
    output:
      type: docker
      image: registry.banco.com/kafka-connect:latest
    plugins:
      - name: debezium-oracle
        artifacts:
          - type: maven
            group: io.debezium
            artifact: debezium-connector-oracle
            version: 2.4.0
```

```json
// Debezium Oracle Connector - CDC desde Bantotal
{
    "name": "bantotal-cdc",
    "config": {
        "connector.class": "io.debezium.connector.oracle.OracleConnector",
        "database.hostname": "oracle-host",
        "database.port": "1521",
        "database.dbname": "BTPROD",
        "database.user": "debezium",
        "database.password": "${file:/opt/kafka/external-config/secrets:oracle-password}",
        "table.include.list": "BANTOTAL.TRANSACCIONES,BANTOTAL.MOVIMIENTOS",
        "topic.prefix": "bt-cdc",
        "schema.history.internal.kafka.bootstrap.servers": "banco-kafka:9092",
        "schema.history.internal.kafka.topic": "schema-changes"
    }
}
```

---

## 4. Kafka Streams

Libreria para procesamiento de streams en tiempo real.

```java
// Ejemplo: Detectar transacciones sospechosas en tiempo real
@Configuration
public class FraudeStreamConfig {

    @Bean
    public KStream<String, Transaccion> fraudeStream(StreamsBuilder builder) {

        KStream<String, Transaccion> transacciones = builder
            .stream("transacciones", Consumed.with(Serdes.String(), transaccionSerde));

        // Agrupar por cliente y ventana de 5 minutos
        KTable<Windowed<String>, Long> conteoVentana = transacciones
            .groupBy((key, tx) -> tx.getClienteId())
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
            .count();

        // Filtrar clientes con mas de 10 transacciones en 5 min
        conteoVentana
            .toStream()
            .filter((windowedKey, count) -> count > 10)
            .mapValues((key, count) -> new AlertaFraude(key.key(), count, "Alta frecuencia"))
            .to("alertas-fraude");

        // Filtrar transacciones de monto alto
        transacciones
            .filter((key, tx) -> tx.getMonto().compareTo(new BigDecimal("50000")) > 0)
            .mapValues(tx -> new AlertaFraude(tx.getClienteId(), 1L, "Monto alto: " + tx.getMonto()))
            .to("alertas-fraude");

        return transacciones;
    }
}
```

---

## 5. Kafka con Camel

```java
// Producer
from("direct:publicarEvento")
    .setHeader(KafkaConstants.KEY, simple("${body.clienteId}"))
    .setHeader(KafkaConstants.PARTITION_KEY, simple("${body.sucursal}"))
    .to("kafka:transacciones?brokers={{kafka.brokers}}"
        + "&serializerClass=org.apache.kafka.common.serialization.StringSerializer"
        + "&keySerializerClass=org.apache.kafka.common.serialization.StringSerializer");

// Consumer
from("kafka:transacciones?brokers={{kafka.brokers}}"
    + "&groupId=servicio-contabilidad"
    + "&autoOffsetReset=earliest"
    + "&consumersCount=3"
    + "&autoCommitEnable=false")
    .routeId("consumidorContabilidad")
    .process(exchange -> {
        String topic = exchange.getIn().getHeader(KafkaConstants.TOPIC, String.class);
        int partition = exchange.getIn().getHeader(KafkaConstants.PARTITION, Integer.class);
        long offset = exchange.getIn().getHeader(KafkaConstants.OFFSET, Long.class);
        log.info("Recibido de {}-{} offset {}", topic, partition, offset);
    })
    .bean(contabilidadService, "registrar")
    .process(exchange -> {
        // Commit manual del offset
        exchange.getIn().setHeader(KafkaConstants.MANUAL_COMMIT, true);
    });
```

---

## 6. Garantias de entrega

| Garantia | Descripcion | Config productor |
|----------|-------------|------------------|
| **At most once** | Puede perder mensajes, nunca duplica | acks=0 |
| **At least once** | Nunca pierde, puede duplicar | acks=all, retries>0 |
| **Exactly once** | Ni pierde ni duplica | enable.idempotence=true + transactional.id |

```java
// Productor con exactly-once semantics
Properties props = new Properties();
props.put("acks", "all");
props.put("enable.idempotence", "true");
props.put("transactional.id", "middleware-tx-001");
props.put("max.in.flight.requests.per.connection", 5);

KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.initTransactions();

try {
    producer.beginTransaction();
    producer.send(new ProducerRecord<>("transacciones", key, value));
    producer.send(new ProducerRecord<>("auditoria", key, auditValue));
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
}
```

---

## 7. Schema Registry y Avro

```java
// Usar Avro + Schema Registry para contratos de datos
// schema: transaccion.avsc
{
    "type": "record",
    "name": "Transaccion",
    "namespace": "com.banco.eventos",
    "fields": [
        {"name": "id", "type": "string"},
        {"name": "clienteId", "type": "string"},
        {"name": "monto", "type": "double"},
        {"name": "tipo", "type": {"type": "enum", "name": "TipoTx", "symbols": ["DEBITO", "CREDITO"]}},
        {"name": "timestamp", "type": "long", "logicalType": "timestamp-millis"}
    ]
}
```

```yaml
# application.yml
spring:
  kafka:
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
    properties:
      schema.registry.url: http://schema-registry:8081
      specific.avro.reader: true
```

---

## Preguntas de entrevista

### Basicas
1. **Diferencia entre Kafka y un broker JMS (AMQ)?**
   - Kafka: log persistente, replay, alto throughput, particiones. JMS: queue/topic clasico, message-level ack, menor latencia individual
2. **Que es un consumer group?**
   - Grupo de consumidores que se reparten particiones. Cada particion la consume un solo miembro del grupo
3. **Que es el offset?**
   - Posicion del mensaje en la particion. El consumidor trackea su offset para saber donde quedo

### Intermedias
4. **Como garantizas exactly-once delivery?**
   - Productor idempotente (enable.idempotence=true) + transactional.id + isolation.level=read_committed
5. **Que es Kafka Connect y cuando lo usas?**
   - Framework para mover datos sin codigo. Usar cuando necesitas CDC, ETL, o sincronizar BD con Kafka
6. **Que es Debezium?**
   - Conector Kafka Connect para CDC: captura cambios en BD (insert/update/delete) y los publica en topics
7. **Como escalas consumidores en Kafka?**
   - Agregar consumidores al grupo (max = numero de particiones). Mas particiones = mas paralelismo

### Avanzadas
8. **Como implementas deteccion de fraude con Kafka Streams?**
   - Windowed aggregation para detectar patrones temporales, KTable para estado, filter por umbrales
9. **Kafka vs AMQ: cuando usar cada uno?**
   - Kafka: event sourcing, CDC, alto volumen, replay. AMQ: request-reply, routing complejo, integracion legacy, baja latencia
10. **Como manejas schema evolution?**
    - Schema Registry + Avro con compatibilidad BACKWARD/FORWARD. Nunca romper compatibilidad en produccion

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| Kafka vs AMQ? | Kafka=streaming/eventos, AMQ=mensajeria clasica/request-reply |
| Consumer group? | Consumidores que se reparten particiones de un topic |
| Exactly-once? | idempotence=true + transactional.id |
| Kafka Connect? | Framework para mover datos entre Kafka y sistemas externos |
| Debezium? | CDC connector: captura cambios de BD → Kafka topics |
| Kafka Streams? | Libreria para procesamiento de streams en tiempo real |
| Schema Registry? | Registro centralizado de schemas (Avro) con versionado |
| AMQ Streams? | Kafka con operador Red Hat para OpenShift |
| Particiones? | Paralelismo: mas particiones = mas consumidores concurrentes |
| Retention? | Cuanto tiempo Kafka guarda los mensajes (default 7 dias) |

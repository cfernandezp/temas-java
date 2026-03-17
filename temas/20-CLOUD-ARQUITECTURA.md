# Cloud y Arquitectura

---

## 1. Spring Batch

Framework para **procesamiento masivo de datos** en lotes (batch). Ideal para: migraciones, reportes nocturnos, ETL, procesamiento de archivos grandes.

```java
// Estructura de un Job en Spring Batch:
// Job → Step(s) → Reader → Processor → Writer

@Configuration
public class MigracionJobConfig {

    @Bean
    public Job migracionJob(JobRepository jobRepository, Step migracionStep) {
        return new JobBuilder("migracionJob", jobRepository)
            .start(migracionStep)
            .build();
    }

    @Bean
    public Step migracionStep(JobRepository jobRepository,
                              PlatformTransactionManager txManager) {
        return new StepBuilder("migracionStep", jobRepository)
            .<ClienteCSV, Cliente>chunk(100, txManager)  // procesar de 100 en 100
            .reader(csvReader())       // leer de archivo CSV
            .processor(transformer())  // transformar datos
            .writer(dbWriter())        // escribir en BD
            .build();
    }

    @Bean
    public FlatFileItemReader<ClienteCSV> csvReader() {
        return new FlatFileItemReaderBuilder<ClienteCSV>()
            .name("csvReader")
            .resource(new ClassPathResource("clientes.csv"))
            .delimited()
            .names("nombre", "email", "telefono")
            .targetType(ClienteCSV.class)
            .build();
    }

    @Bean
    public ItemProcessor<ClienteCSV, Cliente> transformer() {
        return csv -> {
            Cliente cliente = new Cliente();
            cliente.setNombre(csv.getNombre().toUpperCase());
            cliente.setEmail(csv.getEmail().toLowerCase());
            return cliente;
        };
    }

    @Bean
    public JpaItemWriter<Cliente> dbWriter(EntityManagerFactory emf) {
        JpaItemWriter<Cliente> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }
}

// Ejecutar el job
@RestController
public class BatchController {

    @PostMapping("/batch/migrar")
    public String ejecutar() {
        JobParameters params = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();
        jobLauncher.run(migracionJob, params);
        return "Job iniciado";
    }
}
```

> **Tip entrevista:** "Spring Batch lo he usado para migraciones de datos y generacion de reportes. La estructura es Job → Step → Reader/Processor/Writer, y procesa en chunks para no cargar todo en memoria."

---

## 2. Azure API Management (APIM)

```
Azure APIM es un gateway que se pone DELANTE de tus APIs para:
- Seguridad: validar tokens, API keys, certificados
- Rate limiting: limitar requests por tiempo
- Transformacion: modificar headers, body, URLs
- Monitoreo: metricas, logs, analytics
- Versionado: manejar versiones de API

Arquitectura tipica:
  Cliente → Azure APIM → API Gateway (Spring Cloud) → Microservicios
                ↓
          Policies (XML)
```

```xml
<!-- Ejemplo de Policy en APIM -->
<policies>
    <inbound>
        <!-- Validar JWT -->
        <validate-jwt header-name="Authorization" require-scheme="Bearer">
            <issuer-signing-keys>
                <key>{{jwt-signing-key}}</key>
            </issuer-signing-keys>
        </validate-jwt>

        <!-- Validar header obligatorio -->
        <check-header name="X-Client-Id" failed-check-httpcode="400">
            <value>app-mobile</value>
            <value>app-web</value>
        </check-header>

        <!-- Rate limiting -->
        <rate-limit calls="100" renewal-period="60" />

        <!-- Agregar header antes de enviar al backend -->
        <set-header name="X-Forwarded-By" exists-action="override">
            <value>azure-apim</value>
        </set-header>
    </inbound>

    <backend>
        <forward-request />
    </backend>

    <outbound>
        <!-- Remover headers internos de la respuesta -->
        <set-header name="X-Internal-Id" exists-action="delete" />
    </outbound>

    <on-error>
        <return-response>
            <set-status code="500" reason="Internal Error" />
            <set-body>{"error": "Servicio no disponible"}</set-body>
        </return-response>
    </on-error>
</policies>
```

> **Tip entrevista:** "En mi proyecto usamos Azure APIM para centralizar seguridad. Las policies validan JWT, headers obligatorios y rate limiting antes de que el request llegue al backend. Esto nos permite cambiar reglas de seguridad sin tocar el codigo."

---

## 3. AKS (Azure Kubernetes Service)

```
AKS = Kubernetes gestionado por Azure

Conceptos clave:
- Pod: unidad minima, contiene 1+ containers (tu app Spring Boot)
- Deployment: define cuantas replicas del Pod quieres
- Service: expone los Pods internamente (ClusterIP) o externamente (LoadBalancer)
- Ingress: enrutamiento HTTP (rutas → servicios)
- ConfigMap/Secret: configuracion externalizada

Arquitectura tipica con microservicios:
  Internet → Ingress → Service A (3 replicas)
                     → Service B (2 replicas)
                     → Service C (1 replica)
```

```yaml
# deployment.yaml - Spring Boot en Kubernetes
apiVersion: apps/v1
kind: Deployment
metadata:
  name: usuario-service
spec:
  replicas: 3                    # 3 instancias
  selector:
    matchLabels:
      app: usuario-service
  template:
    spec:
      containers:
        - name: usuario-service
          image: miregistry.azurecr.io/usuario-service:1.0
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: db-secrets
                  key: password
          resources:
            requests:
              memory: "256Mi"
              cpu: "250m"
            limits:
              memory: "512Mi"
              cpu: "500m"
          livenessProbe:          # reiniciar si no responde
            httpGet:
              path: /actuator/health
              port: 8080
          readinessProbe:         # no enviar trafico hasta que este listo
            httpGet:
              path: /actuator/health
              port: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: usuario-service
spec:
  selector:
    app: usuario-service
  ports:
    - port: 80
      targetPort: 8080
  type: ClusterIP               # solo accesible internamente
```

---

## 4. Kibana y ELK Stack (Observabilidad)

```
ELK Stack = Elasticsearch + Logstash + Kibana

Flujo:
  App Spring Boot → Logstash (recolecta/transforma logs)
                  → Elasticsearch (indexa y almacena)
                  → Kibana (visualiza, dashboards, alertas)

Alternativa moderna: EFK (Elasticsearch + Fluentd + Kibana)
```

```java
// Spring Boot + Logstash (enviar logs estructurados)
// Dependencia: logstash-logback-encoder

// logback-spring.xml
<configuration>
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>logstash:5000</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"service":"usuario-service"}</customFields>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="LOGSTASH" />
    </root>
</configuration>
```

```
En Kibana puedes:
- Buscar logs por servicio, nivel, timestamp, mensaje
- Crear dashboards con metricas (errores/hora, latencia, requests)
- Configurar alertas (ej: si errores > 50 en 5 minutos → notificar)
- Correlacionar logs entre microservicios usando traceId
```

> **Tip entrevista:** "Usamos ELK Stack para centralizar logs de todos los microservicios. Kibana nos permite buscar, filtrar y crear dashboards. Cada log tiene un traceId para seguir un request a traves de multiples servicios."

---

## 5. Azure Databricks

```
Databricks = Plataforma de datos unificada basada en Apache Spark

Para que se usa:
- Procesamiento de datos masivos (ETL)
- Data engineering (pipelines de datos)
- Machine learning
- Analytics y BI

En el contexto de un Java Developer:
- Spring Batch procesa datos dentro de la app
- Databricks procesa datos a gran escala FUERA de la app
- Se conecta con Azure Data Lake, SQL Database, Cosmos DB
- Notebooks en Python/Scala/SQL para transformar datos

Flujo tipico:
  Datos raw (Data Lake) → Databricks (ETL/transformacion) → BD limpia → API Spring Boot
```

---

## 6. Arquitectura de microservicios en cloud

```
Arquitectura tipica en Azure:

                    ┌─────────────┐
  Internet ───────→ │ Azure APIM  │  (seguridad, rate limit)
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ AKS Cluster │
                    │             │
                    │ ┌─────────┐ │
                    │ │ Ingress │ │  (enrutamiento)
                    │ └────┬────┘ │
                    │      │      │
                    │ ┌────▼────┐ │
                    │ │ Gateway │ │  (Spring Cloud Gateway)
                    │ └────┬────┘ │
                    │      │      │
                    │ ┌────▼─────┐│
                    │ │Services: ││
                    │ │ Users    ││ ←→ Azure SQL Database
                    │ │ Orders   ││ ←→ Cosmos DB (MongoDB)
                    │ │ Payments ││ ←→ Azure Service Bus (mensajeria)
                    │ └──────────┘│
                    └─────────────┘
                           │
                    ┌──────▼──────┐
                    │ Observability│
                    │ ELK/Kibana  │
                    │ App Insights│
                    └─────────────┘
```

---

## 7. On-premise vs Cloud

| Aspecto | On-premise | Cloud (Azure/AWS/GCP) |
|---------|-----------|----------------------|
| Infraestructura | Servidores propios | Servidores del proveedor |
| Costo | CAPEX (inversion inicial alta) | OPEX (pagas por uso) |
| Escalabilidad | Manual (comprar hardware) | Automatica (auto-scaling) |
| Mantenimiento | Tu equipo | El proveedor gestiona |
| Deploy | Jenkins → servidor fisico | CI/CD → AKS/ECS |
| Ejemplo | Banco con datacenter propio | Startup en AWS |

> **Tip entrevista:** "La ventaja principal del cloud es la escalabilidad automatica y el modelo de pago por uso. Pero para industrias reguladas como banca, a veces se usa un modelo hibrido: datos sensibles on-premise y servicios no criticos en cloud."

---

## RESPUESTAS RAPIDAS - CLOUD & ARQUITECTURA

| Pregunta | Respuesta |
|----------|-----------|
| Que es Spring Batch? | Framework para procesamiento masivo en lotes: Job → Step → Reader/Processor/Writer |
| Que es Azure APIM? | API Gateway que gestiona seguridad, rate limiting y transformaciones con policies XML |
| Que es AKS? | Kubernetes gestionado por Azure. Orquesta contenedores Docker de microservicios |
| Que es ELK Stack? | Elasticsearch + Logstash + Kibana para centralizar y visualizar logs |
| Que es Kibana? | Herramienta de visualizacion para buscar logs, crear dashboards y alertas |
| Que es Databricks? | Plataforma de procesamiento masivo de datos basada en Spark (ETL, analytics) |
| Pod vs Container? | Pod es la unidad de Kubernetes que contiene 1+ containers |
| On-premise vs Cloud? | On-premise: servidores propios (CAPEX). Cloud: servidores del proveedor (OPEX, auto-scaling) |

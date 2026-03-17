# MICROSERVICIOS

---

## 1. Que son?

Arquitectura que divide la aplicacion en **servicios pequenos e independientes**, cada uno con su propia logica, BD y despliegue.

**Monolito vs Microservicios:**

| | Monolito | Microservicios |
|---|---|---|
| Deploy | Todo junto | Cada servicio independiente |
| Escalabilidad | Vertical (mas CPU/RAM) | Horizontal (mas instancias del servicio que lo necesita) |
| BD | Una compartida | Una por servicio (Database per Service) |
| Equipo | Todos tocan todo | Cada equipo es dueno de su servicio |
| Complejidad | Simple al inicio | Mayor complejidad operacional |
| Cuando usar | Equipos pequenos, MVP | Equipos grandes, alta escala |

---

## 2. Ecosistema Spring Cloud

| Componente | Herramienta | Para que |
|-----------|-------------|----------|
| Servicio | Spring Boot | Cada microservicio individual |
| API Gateway | Spring Cloud Gateway | Punto de entrada unico, routing, seguridad |
| Service Discovery | Eureka / Consul | Registro y descubrimiento de servicios |
| Config Server | Spring Cloud Config | Configuracion centralizada |
| Circuit Breaker | Resilience4j | Tolerancia a fallos |
| Mensajeria | Kafka / RabbitMQ | Comunicacion asincrona |
| Seguridad | JWT + OAuth2 | Autenticacion/autorizacion |
| Trazabilidad | Zipkin / Micrometer | Rastreo de requests entre servicios |
| Load Balancer | Spring Cloud LoadBalancer | Distribuir carga entre instancias |

---

## 3. Comunicacion entre microservicios

### Sincrona (HTTP)
```java
// WebClient (reactivo, recomendado)
@Service
public class PedidoService {
    private final WebClient webClient;

    public PedidoService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://usuario-service").build();
    }

    public Mono<UsuarioDTO> obtenerUsuario(Long id) {
        return webClient.get()
            .uri("/api/usuarios/{id}", id)
            .retrieve()
            .bodyToMono(UsuarioDTO.class);
    }
}
```

### Asincrona (Kafka/RabbitMQ)
```java
// Productor (envia evento)
@Service
public class PedidoService {
    private final KafkaTemplate<String, PedidoEvent> kafka;

    public void crearPedido(PedidoDTO dto) {
        Pedido pedido = repository.save(toEntity(dto));
        kafka.send("pedidos", new PedidoEvent(pedido.getId(), "CREADO"));
    }
}

// Consumidor (otro microservicio)
@Service
public class NotificacionService {
    @KafkaListener(topics = "pedidos", groupId = "notificaciones")
    public void onPedidoCreado(PedidoEvent event) {
        enviarEmail("Pedido " + event.getId() + " creado");
    }
}
```

> **Tip entrevista:** "Uso comunicacion sincrona (HTTP) cuando necesito respuesta inmediata. Asincrona (Kafka) cuando el servicio que recibe puede procesarlo despues, logrando **desacoplamiento** y **resiliencia**."

---

## 4. API Gateway

Punto de entrada unico para todos los microservicios.

```yaml
# application.yml del Gateway
spring:
  cloud:
    gateway:
      routes:
        - id: usuario-service
          uri: lb://USUARIO-SERVICE
          predicates:
            - Path=/api/usuarios/**
        - id: pedido-service
          uri: lb://PEDIDO-SERVICE
          predicates:
            - Path=/api/pedidos/**
```

**Responsabilidades:**
- **Routing**: redirige requests al microservicio correcto
- **Autenticacion**: valida JWT antes de pasar la peticion
- **Rate Limiting**: limita peticiones por cliente
- **Load Balancing**: distribuye carga entre instancias

---

## 5. Circuit Breaker (Resilience4j)

Previene **fallos en cascada**: si un servicio falla repetidamente, deja de llamarlo.

**3 estados:**
1. **CLOSED** (normal): llamadas pasan al servicio
2. **OPEN** (fallo detectado): llamadas se rechazan, retorna fallback
3. **HALF-OPEN** (prueba): permite algunas llamadas para verificar recuperacion

```java
@CircuitBreaker(name = "usuarioService", fallbackMethod = "fallback")
@Retry(name = "usuarioService")
public UsuarioDTO obtenerUsuario(Long id) {
    return webClient.get()
        .uri("/api/usuarios/{id}", id)
        .retrieve()
        .bodyToMono(UsuarioDTO.class)
        .block();
}

public UsuarioDTO fallback(Long id, Exception e) {
    log.warn("Fallback para usuario {}: {}", id, e.getMessage());
    return new UsuarioDTO(id, "Usuario no disponible", null);
}
```

**Otros patrones de Resilience4j:**
- **Retry**: reintentos con backoff exponencial
- **Rate Limiter**: limitar llamadas por segundo
- **Bulkhead**: aislar recursos (pool de hilos separado por servicio)
- **Time Limiter**: timeout maximo por llamada

---

## 6. Service Discovery (Eureka)

```java
// En cada microservicio:
@SpringBootApplication
@EnableDiscoveryClient
public class UsuarioServiceApplication { }

// application.yml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

> Los servicios se registran en Eureka con un nombre (ej: `USUARIO-SERVICE`). Otros servicios lo encuentran por ese nombre, sin hardcodear IPs.

---

## 7. Seguridad con JWT

```
1. Cliente envia credenciales → Auth Service
2. Auth Service valida → genera JWT
3. Cliente envia JWT en cada request → API Gateway
4. Gateway valida JWT → pasa request al microservicio
```

```java
// Generar JWT
String token = Jwts.builder()
    .setSubject(usuario.getEmail())
    .claim("roles", usuario.getRoles())
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
    .signWith(SignatureAlgorithm.HS256, secretKey)
    .compact();

// Validar JWT (en filtro del Gateway)
Claims claims = Jwts.parser()
    .setSigningKey(secretKey)
    .parseClaimsJws(token)
    .getBody();
```

---

## RESPUESTAS RAPIDAS - MICROSERVICIOS

| Pregunta | Respuesta |
|----------|-----------|
| Que son microservicios? | Servicios pequenos e independientes, cada uno con su BD y deploy |
| Cuando NO usar microservicios? | Equipos pequenos, MVP, cuando la complejidad operacional no se justifica |
| Database per Service? | Cada microservicio tiene su propia BD, no comparte con otros |
| Comunicacion sincrona vs asincrona? | HTTP para respuesta inmediata, Kafka/RabbitMQ para desacoplamiento |
| Que es API Gateway? | Punto de entrada unico: routing, seguridad, rate limiting |
| Que es Service Discovery? | Registro dinamico de servicios (Eureka), evita hardcodear IPs |
| Que es Circuit Breaker? | Previene fallos en cascada: si falla, deja de llamar y retorna fallback |
| Como aseguras microservicios? | JWT + OAuth2, validacion en el Gateway |

# SPRING BOOT

---

## 1. Anotaciones principales

| Anotacion | Uso |
|-----------|-----|
| `@SpringBootApplication` | Clase principal (combina @Configuration, @EnableAutoConfiguration, @ComponentScan) |
| `@RestController` | Controller que retorna JSON (combina @Controller + @ResponseBody) |
| `@Service` | Capa de logica de negocio |
| `@Repository` | Capa de acceso a datos |
| `@Component` | Bean generico de Spring |
| `@Autowired` | Inyeccion de dependencias (preferir inyeccion por constructor) |
| `@GetMapping` / `@PostMapping` | Mapeo de endpoints HTTP |
| `@PathVariable` | Parametro en la URL: `/usuarios/{id}` |
| `@RequestParam` | Parametro query: `/usuarios?nombre=Ana` |
| `@RequestBody` | Body del request (JSON → objeto) |
| `@Valid` | Activa validaciones del DTO |
| `@Transactional` | Manejo de transacciones de BD |

---

## 2. Arquitectura en capas

```
Controller (@RestController)    ← recibe HTTP requests, valida input
    ↓
Service (@Service)              ← logica de negocio
    ↓
Repository (@Repository)        ← acceso a datos (JPA/Hibernate)
    ↓
Database                        ← BD relacional o NoSQL
```

```java
// Controller
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) { this.service = service; }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody CrearUsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }
}

// Service
@Service
public class UsuarioService {
    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) { this.repo = repo; }

    public UsuarioDTO buscarPorId(Long id) {
        return repo.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Transactional
    public UsuarioDTO crear(CrearUsuarioDTO dto) {
        Usuario entity = toEntity(dto);
        return toDTO(repo.save(entity));
    }
}

// Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.edad > :edad")
    List<Usuario> findMayoresQue(@Param("edad") int edad);
}
```

---

## 3. Anotaciones de Spring avanzadas

| Anotacion | Que hace |
|-----------|----------|
| `@ControllerAdvice` | Manejo global de excepciones |
| `@CrossOrigin` | Habilita CORS para un controller |
| `@Value("${app.nombre}")` | Inyecta valor de application.properties |
| `@ConfigurationProperties` | Mapea grupo de propiedades a una clase |
| `@Profile("dev")` | Activa bean solo en cierto perfil |
| `@Order(1)` | Define orden de ejecucion de filtros/beans |
| `@PreAuthorize("hasRole('ADMIN')")` | Seguridad a nivel de metodo |
| `@CachePut` / `@Cacheable` | Cache de resultados |
| `@Scheduled` | Tareas programadas |
| `@Async` | Ejecucion asincrona |

---

## 4. Scopes de beans

| Scope | Descripcion |
|-------|-------------|
| `singleton` (default) | Una sola instancia en todo el contexto |
| `prototype` | Nueva instancia cada vez que se solicita |
| `request` | Una instancia por HTTP request |
| `session` | Una instancia por sesion HTTP |

```java
@Service
@Scope("prototype")
public class ReporteService { /* nueva instancia cada vez */ }
```

---

## 5. Profiles

```yaml
# application.yml
spring:
  profiles:
    active: dev

---
# application-dev.yml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:h2:mem:testdb

---
# application-prod.yml
server:
  port: 80
spring:
  datasource:
    url: jdbc:postgresql://prod-server:5432/mydb
```

```java
@Configuration
@Profile("dev")
public class DevConfig {
    @Bean
    public DataSource dataSource() { /* H2 en memoria */ }
}
```

---

## 6. Spring Security basico

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

## 7. Estructura tipica de proyecto Spring Boot

```
mi-proyecto/
├── src/main/java/com/empresa/proyecto/
│   ├── Application.java                    ← @SpringBootApplication (main)
│   ├── config/
│   │   ├── SecurityConfig.java             ← configuracion de seguridad
│   │   └── CorsConfig.java                ← configuracion CORS
│   ├── controller/
│   │   └── UsuarioController.java          ← @RestController (endpoints)
│   ├── service/
│   │   ├── UsuarioService.java             ← interfaz del servicio
│   │   └── impl/
│   │       └── UsuarioServiceImpl.java     ← @Service (logica de negocio)
│   ├── repository/
│   │   └── UsuarioRepository.java          ← @Repository (JPA)
│   ├── model/
│   │   ├── entity/
│   │   │   └── Usuario.java                ← @Entity (tabla BD)
│   │   └── dto/
│   │       ├── UsuarioDTO.java             ← respuesta
│   │       └── CrearUsuarioDTO.java        ← request con validaciones
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java     ← @ControllerAdvice
│   │   └── UsuarioNoEncontradoException.java
│   └── mapper/
│       └── UsuarioMapper.java              ← entity ↔ DTO
├── src/main/resources/
│   ├── application.yml                     ← configuracion principal
│   ├── application-dev.yml                ← config desarrollo
│   └── application-prod.yml               ← config produccion
├── src/test/java/
│   └── com/empresa/proyecto/
│       ├── service/
│       │   └── UsuarioServiceTest.java     ← test unitario
│       └── controller/
│           └── UsuarioControllerIT.java    ← test integracion
└── pom.xml
```

> **Tip entrevista:** "Sigo la arquitectura en capas: Controller recibe requests, Service tiene la logica de negocio, Repository accede a datos. Los DTOs separan la representacion externa de las entidades internas. Uso @ControllerAdvice centralizado para manejar excepciones."

---

## 8. Inyeccion de Dependencias (DI)

```java
// QUE ES: Spring crea los objetos (@Bean) y los "inyecta" donde se necesiten
// No haces new UsuarioService() manualmente — Spring lo gestiona

// 3 formas de inyectar:

// 1. POR CONSTRUCTOR (RECOMENDADO)
@Service
public class UsuarioService {
    private final UsuarioRepository repository;  // final = inmutable

    // Spring inyecta automaticamente el repository
    // Si hay un solo constructor, no necesitas @Autowired
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }
}

// 2. POR CAMPO (NO recomendado - dificil de testear)
@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository;
    // No se puede hacer final, no se puede inyectar en tests facilmente
}

// 3. POR SETTER (raro, para dependencias opcionales)
@Service
public class UsuarioService {
    private UsuarioRepository repository;

    @Autowired
    public void setRepository(UsuarioRepository repository) {
        this.repository = repository;
    }
}
```

> **Tip entrevista:** "Siempre uso inyeccion por constructor porque permite declarar dependencias como final (inmutables), facilita los tests unitarios (pasas mocks por constructor), y falla rapido si falta alguna dependencia."

---

## 9. @SpringBootApplication en detalle

```java
// @SpringBootApplication combina 3 anotaciones:
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Equivale a:
@Configuration           // esta clase es fuente de @Bean
@EnableAutoConfiguration  // configura automaticamente segun dependencias del classpath
@ComponentScan           // escanea @Component, @Service, @Repository, @Controller
                         // en este paquete y sub-paquetes
public class Application { ... }

// ¿Que hace SpringApplication.run()?
// 1. Crea el ApplicationContext (contenedor de beans)
// 2. Escanea componentes (@ComponentScan)
// 3. Auto-configura segun el classpath (si hay spring-data-jpa → configura JPA)
// 4. Inicia servidor embebido (Tomcat/Netty)
// 5. Ejecuta CommandLineRunner / ApplicationRunner si existen
```

---

## 10. Aplicaciones reactivas vs tradicionales

| Aspecto | Spring MVC (tradicional) | Spring WebFlux (reactivo) |
|---------|------------------------|--------------------------|
| Servidor | Tomcat (embebido) | Netty |
| Thread model | 1 thread por request | Event loop (pocos threads) |
| Retorno | Objetos directos | Mono / Flux |
| BD | JPA/Hibernate (bloqueante) | R2DBC (no bloqueante) |
| Dependencia | spring-boot-starter-web | spring-boot-starter-webflux |
| Cuando usar | CRUD, baja-media concurrencia | Alta concurrencia, streaming |

```java
// NO puedes mezclar spring-boot-starter-web y webflux en el mismo proyecto
// Si incluyes ambos, Spring MVC toma prioridad

// Para app reactiva COMPLETA, toda la cadena debe ser no bloqueante:
// Controller (WebFlux) → Service (reactivo) → Repository (R2DBC, no JPA)

// Si usas JPA con WebFlux, bloqueas el event loop → peor rendimiento
// Solucion: usar R2DBC o Reactive MongoDB
```

---

## RESPUESTAS RAPIDAS - SPRING BOOT

| Pregunta | Respuesta |
|----------|-----------|
| Que es Spring Boot? | Framework que simplifica la configuracion de Spring con auto-configuracion y servidor embebido |
| @Controller vs @RestController? | @RestController = @Controller + @ResponseBody (retorna JSON directamente) |
| Como inyectas dependencias? | Preferir inyeccion por constructor (no @Autowired en campo) |
| Que es @Transactional? | Maneja transacciones: si falla algo, hace rollback automatico |
| Que son los profiles? | Configuraciones por ambiente (dev, staging, prod) |
| Scope default de un bean? | Singleton (una sola instancia) |
| Como manejas excepciones? | @ControllerAdvice + @ExceptionHandler centralizado |
| @Query para que? | Queries custom en JPA cuando el naming convention no alcanza |
| Estructura de proyecto? | controller/ service/ repository/ model/entity/ model/dto/ exception/ config/ |
| Inyeccion por constructor? | Recomendada: permite final, testeable, falla rapido si falta dependencia |
| @SpringBootApplication? | Combina @Configuration + @EnableAutoConfiguration + @ComponentScan |
| WebFlux vs MVC? | MVC: bloqueante, Tomcat. WebFlux: reactivo, Netty. No mezclar ambos |

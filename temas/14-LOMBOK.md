# LOMBOK y Anotaciones de DTO

---

## 1. Que es Lombok?

Libreria que **genera codigo repetitivo** (boilerplate) en tiempo de compilacion: getters, setters, constructores, toString, equals, hashCode, builders.

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

---

## 2. Anotaciones principales

```java
// SIN Lombok: 50+ lineas de codigo
public class Usuario {
    private Long id;
    private String nombre;
    private String email;

    public Usuario() {}
    public Usuario(Long id, String nombre, String email) { ... }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    // ... mas getters/setters, toString, equals, hashCode
}

// CON Lombok: 6 lineas
@Data  // genera getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long id;
    private String nombre;
    private String email;
}
```

**Tabla de anotaciones:**

| Anotacion | Que genera |
|-----------|-----------|
| `@Getter` | Getters para todos los campos |
| `@Setter` | Setters para todos los campos |
| `@ToString` | Metodo `toString()` |
| `@EqualsAndHashCode` | Metodos `equals()` y `hashCode()` |
| `@NoArgsConstructor` | Constructor sin parametros |
| `@AllArgsConstructor` | Constructor con todos los parametros |
| `@RequiredArgsConstructor` | Constructor con campos `final` |
| `@Data` | Combina: @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor |
| `@Builder` | Patron Builder para construir objetos |
| `@Slf4j` | Genera `private static final Logger log = ...` |
| `@Value` | Clase inmutable (todos los campos `final`, sin setters) |

---

## 3. @Builder - Patron Builder automatico

Permite construir objetos paso a paso sin constructores con muchos parametros.

```java
@Builder
@Data
public class PedidoDTO {
    private String producto;
    private int cantidad;
    private double precio;
    private String direccion;
    private String metodoPago;
}

// Uso: construccion fluida y legible
PedidoDTO pedido = PedidoDTO.builder()
    .producto("Laptop")
    .cantidad(1)
    .precio(999.99)
    .direccion("Av. Principal 123")
    .metodoPago("tarjeta")
    .build();
```

> **Tip entrevista:** "@Builder permite construir objetos complejos paso a paso, sin necesitar un constructor con 10 parametros. Es muy util para DTOs y para crear objetos en tests."

---

## 4. Anotaciones tipicas de un DTO

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearUsuarioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "Nombre entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email invalido")
    private String email;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "Debe ser mayor de 18")
    @Max(value = 120, message = "Edad maxima 120")
    private Integer edad;

    @Pattern(regexp = "^[0-9]{8}$", message = "DNI debe tener 8 digitos")
    private String dni;
}

// En el Controller se activa con @Valid
@PostMapping
public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody CrearUsuarioDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
}
```

**Anotaciones de validacion (javax.validation):**

| Anotacion | Valida |
|-----------|--------|
| `@NotNull` | No sea null |
| `@NotBlank` | No sea null, no vacio, no solo espacios |
| `@NotEmpty` | No sea null ni vacio |
| `@Size(min, max)` | Tamano de string o coleccion |
| `@Min` / `@Max` | Valor numerico minimo/maximo |
| `@Email` | Formato de email valido |
| `@Pattern(regexp)` | Cumple expresion regular |
| `@Past` / `@Future` | Fecha en pasado/futuro |
| `@Positive` | Numero positivo |

---

## 5. @Slf4j - Logging automatico

```java
@Slf4j
@Service
public class UsuarioService {

    public UsuarioDTO buscarPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);

        return repository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> {
                log.error("Usuario no encontrado con ID: {}", id);
                return new UsuarioNoEncontradoException(id);
            });
    }
}
// Sin @Slf4j tendrias que escribir:
// private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
```

---

## 6. @Value de Lombok (clase inmutable)

```java
@Value  // todos los campos son final, no hay setters
public class RespuestaDTO {
    String mensaje;
    int codigo;
    LocalDateTime timestamp;
}

// Equivale a:
public final class RespuestaDTO {
    private final String mensaje;
    private final int codigo;
    private final LocalDateTime timestamp;
    // solo getters, sin setters → inmutable
}
```

> **Tip entrevista:** "Uso `@Data` para entidades mutables y `@Value` para DTOs de respuesta inmutables. Para Java 17+ uso `record` en vez de @Value."

---

## 7. Entidad JPA con Lombok (ejemplo completo)

```java
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Column(name = "stock")
    private int cantidad;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;
}
```

---

## RESPUESTAS RAPIDAS - LOMBOK

| Pregunta | Respuesta |
|----------|-----------|
| Que es Lombok? | Libreria que genera getters, setters, constructores, toString, etc. en tiempo de compilacion |
| Que hace @Data? | Combina @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor |
| Que permite @Builder? | Construir objetos paso a paso con patron Builder, sin constructores de 10 parametros |
| @Data vs @Value? | @Data: mutable (con setters). @Value: inmutable (sin setters, campos final) |
| Anotaciones de validacion en DTO? | @NotBlank, @Email, @Size, @Min, @Max, @Pattern, se activan con @Valid en controller |
| Que hace @Slf4j? | Genera automaticamente `private static final Logger log` para logging |

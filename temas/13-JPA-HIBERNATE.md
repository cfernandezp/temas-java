# JPA e Hibernate

---

## 1. Que es JPA?

**Java Persistence API** es una especificacion de Java para mapear objetos Java a tablas de base de datos (ORM). **Hibernate** es la implementacion mas usada de JPA.

```
JPA (especificacion/interfaz)
 └── Hibernate (implementacion)
 └── EclipseLink (otra implementacion)
```

> **Tip entrevista:** "JPA es la especificacion (las reglas), Hibernate es quien las implementa. Es como `List` (interfaz) vs `ArrayList` (implementacion)."

---

## 2. Entidad JPA

Una **entidad** es una clase Java mapeada a una tabla de la BD. Se marca con `@Entity`.

```java
@Entity
@Table(name = "usuarios")  // nombre de la tabla (opcional si coincide)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombre;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Estado estado; // ACTIVO, INACTIVO → guarda el texto, no el ordinal

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    // getters y setters
}
```

**Anotaciones clave de entidad:**

| Anotacion | Que hace |
|-----------|----------|
| `@Entity` | Marca la clase como entidad JPA |
| `@Table(name = "...")` | Nombre de la tabla en BD |
| `@Id` | Define la clave primaria |
| `@GeneratedValue` | Auto-generacion del ID |
| `@Column` | Configuracion de columna (nombre, nullable, unique, length) |
| `@Enumerated` | Mapea un enum (STRING o ORDINAL) |
| `@Transient` | Campo que NO se persiste en BD |
| `@CreationTimestamp` | Fecha de creacion automatica |
| `@Lob` | Para campos grandes (texto largo, archivos) |

---

## 3. Relaciones entre entidades

```java
// ONE TO MANY: Un usuario tiene muchos pedidos
@Entity
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();
}

// MANY TO ONE: Muchos pedidos pertenecen a un usuario
@Entity
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")  // FK en la tabla pedidos
    private Usuario usuario;
}

// MANY TO MANY: Estudiantes y Cursos
@Entity
public class Estudiante {
    @ManyToMany
    @JoinTable(
        name = "estudiante_curso",          // tabla intermedia
        joinColumns = @JoinColumn(name = "estudiante_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private Set<Curso> cursos = new HashSet<>();
}
```

| Relacion | Ejemplo | Anotacion |
|----------|---------|-----------|
| One to One | Usuario ↔ Perfil | `@OneToOne` |
| One to Many | Usuario → Pedidos | `@OneToMany` |
| Many to One | Pedidos → Usuario | `@ManyToOne` |
| Many to Many | Estudiantes ↔ Cursos | `@ManyToMany` |

> **Tip entrevista:** "Siempre uso `FetchType.LAZY` para evitar el problema N+1. Cargo las relaciones solo cuando las necesito con `JOIN FETCH` en la query."

---

## 4. Metodos mas usados de JpaRepository

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // JpaRepository ya incluye:
    // save(entity)           → INSERT o UPDATE
    // findById(id)           → SELECT por ID, retorna Optional
    // findAll()              → SELECT *
    // deleteById(id)         → DELETE por ID
    // count()                → COUNT(*)
    // existsById(id)         → EXISTS

    // Derived queries (Spring genera el SQL automaticamente)
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByEstado(Estado estado);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByEdadBetween(int min, int max);
    List<Usuario> findByEstadoOrderByNombreAsc(Estado estado);
    boolean existsByEmail(String email);
    long countByEstado(Estado estado);

    // JPQL custom
    @Query("SELECT u FROM Usuario u WHERE u.edad > :edad AND u.estado = :estado")
    List<Usuario> buscarActivosMayores(@Param("edad") int edad, @Param("estado") Estado estado);

    // Query nativa SQL
    @Query(value = "SELECT * FROM usuarios WHERE YEAR(fecha_creacion) = :anio", nativeQuery = true)
    List<Usuario> buscarPorAnio(@Param("anio") int anio);

    // Modificacion con @Modifying
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.estado = :estado WHERE u.id = :id")
    int actualizarEstado(@Param("id") Long id, @Param("estado") Estado estado);
}
```

---

## 5. Transacciones en JPA (Spring Boot)

```java
@Service
public class TransferenciaService {

    private final CuentaRepository cuentaRepo;

    @Transactional  // si algo falla, hace ROLLBACK de todo
    public void transferir(Long origenId, Long destinoId, BigDecimal monto) {
        Cuenta origen = cuentaRepo.findById(origenId)
            .orElseThrow(() -> new CuentaNoEncontradaException(origenId));
        Cuenta destino = cuentaRepo.findById(destinoId)
            .orElseThrow(() -> new CuentaNoEncontradaException(destinoId));

        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        origen.setSaldo(origen.getSaldo().subtract(monto));  // debita
        destino.setSaldo(destino.getSaldo().add(monto));      // acredita

        cuentaRepo.save(origen);
        cuentaRepo.save(destino);
        // si falla el segundo save, se revierte el primero tambien
    }
}
```

**Propiedades de @Transactional:**

| Propiedad | Uso |
|-----------|-----|
| `readOnly = true` | Optimiza lectura, no permite escritura |
| `rollbackFor = Exception.class` | Rollback en cualquier excepcion |
| `propagation = REQUIRED` | Default: usa transaccion existente o crea nueva |
| `propagation = REQUIRES_NEW` | Siempre crea nueva transaccion |
| `isolation = READ_COMMITTED` | Nivel de aislamiento |

> **Tip entrevista:** "En Spring Boot uso `@Transactional` a nivel de servicio. Para lecturas uso `@Transactional(readOnly = true)` que optimiza el rendimiento. Nunca pongo @Transactional en el controller."

---

## 6. Transacciones con JDBC puro (sin JPA)

```java
Connection con = null;
try {
    con = DriverManager.getConnection(url, user, pass);
    con.setAutoCommit(false);  // inicio de transaccion

    PreparedStatement ps1 = con.prepareStatement(
        "UPDATE cuentas SET saldo = saldo - ? WHERE id = ?");
    ps1.setBigDecimal(1, monto);
    ps1.setLong(2, origenId);
    ps1.executeUpdate();

    PreparedStatement ps2 = con.prepareStatement(
        "UPDATE cuentas SET saldo = saldo + ? WHERE id = ?");
    ps2.setBigDecimal(1, monto);
    ps2.setLong(2, destinoId);
    ps2.executeUpdate();

    con.commit();  // confirmar ambas operaciones

} catch (SQLException e) {
    if (con != null) con.rollback();  // revertir todo si falla
    throw e;
} finally {
    if (con != null) con.setAutoCommit(true);
    if (con != null) con.close();
}
```

> **Tip entrevista:** "Con JDBC manejas transacciones manualmente con `setAutoCommit(false)`, `commit()` y `rollback()`. Con Spring Boot + JPA, `@Transactional` hace todo esto automaticamente."

---

## 7. Fetch LAZY vs EAGER y problema N+1

```java
// LAZY: NO carga los pedidos hasta que los accedas
@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
private List<Pedido> pedidos;

// EAGER: carga los pedidos SIEMPRE junto con el usuario
@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
private List<Pedido> pedidos;

// PROBLEMA N+1: si cargo 100 usuarios con LAZY y accedo a pedidos de cada uno
// se ejecutan 101 queries: 1 para usuarios + 100 para pedidos de cada uno

// SOLUCION: JOIN FETCH en la query
@Query("SELECT u FROM Usuario u JOIN FETCH u.pedidos WHERE u.estado = :estado")
List<Usuario> findActivosConPedidos(@Param("estado") Estado estado);
```

---

## RESPUESTAS RAPIDAS - JPA

| Pregunta | Respuesta |
|----------|-----------|
| Que es JPA? | Especificacion Java para ORM (mapear objetos a tablas). Hibernate es su implementacion |
| Como indico que una clase es entidad? | Con `@Entity` + `@Id` para la clave primaria |
| Metodos mas usados de JPA? | `save()`, `findById()`, `findAll()`, `deleteById()`, `existsById()` |
| Como manejas transacciones? | `@Transactional` en el Service. Si falla, rollback automatico |
| LAZY vs EAGER? | LAZY carga bajo demanda (recomendado). EAGER carga siempre (puede causar N+1) |
| Que es N+1? | Problema de performance: 1 query + N queries adicionales por relacion. Solucion: JOIN FETCH |
| @Transactional readOnly? | Optimiza consultas de lectura, no permite escritura |

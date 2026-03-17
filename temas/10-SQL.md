# SQL - Preguntas de Entrevista

---

## 1. Tipos de comandos SQL

| Tipo | Comandos | Que hace |
|------|----------|----------|
| **DDL** (Data Definition) | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` | Define estructura de tablas |
| **DML** (Data Manipulation) | `INSERT`, `UPDATE`, `DELETE`, `SELECT` | Manipula datos |
| **DCL** (Data Control) | `GRANT`, `REVOKE` | Permisos |
| **TCL** (Transaction Control) | `COMMIT`, `ROLLBACK`, `SAVEPOINT` | Transacciones |

---

## 2. JOINs

```sql
-- INNER JOIN: solo registros que coinciden en ambas tablas
SELECT u.nombre, p.titulo
FROM usuarios u
INNER JOIN pedidos p ON u.id = p.usuario_id;

-- LEFT JOIN: todos los de la izquierda + coincidencias de la derecha
SELECT u.nombre, p.titulo
FROM usuarios u
LEFT JOIN pedidos p ON u.id = p.usuario_id;
-- usuarios sin pedidos aparecen con NULL en titulo

-- RIGHT JOIN: todos los de la derecha + coincidencias de la izquierda
-- FULL JOIN: todos de ambas tablas
```

| JOIN | Resultado |
|------|-----------|
| `INNER JOIN` | Solo coincidencias |
| `LEFT JOIN` | Todo de la izquierda + coincidencias |
| `RIGHT JOIN` | Todo de la derecha + coincidencias |
| `FULL JOIN` | Todo de ambas tablas |
| `CROSS JOIN` | Producto cartesiano (todas las combinaciones) |

---

## 3. Funciones de agregacion

```sql
SELECT
    departamento,
    COUNT(*) as total_empleados,
    AVG(salario) as salario_promedio,
    MAX(salario) as salario_maximo,
    MIN(salario) as salario_minimo,
    SUM(salario) as total_salarios
FROM empleados
GROUP BY departamento
HAVING AVG(salario) > 5000   -- filtrar DESPUES de agrupar
ORDER BY salario_promedio DESC;
```

> **WHERE vs HAVING:** WHERE filtra filas ANTES de agrupar. HAVING filtra DESPUES de agrupar.

---

## 4. Subqueries

```sql
-- Empleados con salario mayor al promedio
SELECT nombre, salario
FROM empleados
WHERE salario > (SELECT AVG(salario) FROM empleados);

-- Departamentos con mas de 5 empleados
SELECT departamento
FROM empleados
GROUP BY departamento
HAVING COUNT(*) > 5;

-- EXISTS: verifica si subquery retorna resultados
SELECT nombre FROM usuarios u
WHERE EXISTS (
    SELECT 1 FROM pedidos p WHERE p.usuario_id = u.id
);
```

---

## 5. Indices

```sql
-- Crear indice (acelera SELECT, ralentiza INSERT/UPDATE)
CREATE INDEX idx_email ON usuarios(email);

-- Indice unico
CREATE UNIQUE INDEX idx_dni ON usuarios(dni);

-- Indice compuesto
CREATE INDEX idx_nombre_apellido ON usuarios(nombre, apellido);
```

> **Tip entrevista:** "Los indices aceleran las consultas SELECT pero ralentizan INSERT/UPDATE porque deben mantenerse actualizados. Los creo en columnas que se usan frecuentemente en WHERE, JOIN y ORDER BY."

---

## 6. Alias y division con decimales

```sql
-- Alias de tabla y columna
SELECT e.nombre AS empleado, d.nombre AS departamento
FROM empleados e
JOIN departamentos d ON e.depto_id = d.id;

-- Division con decimales (evitar division entera)
SELECT
    nombre,
    CAST(nota_total AS DECIMAL(10,2)) / cantidad_examenes AS promedio
FROM estudiantes;

-- Alternativa
SELECT nombre, nota_total * 1.0 / cantidad_examenes AS promedio
FROM estudiantes;
```

---

## 7. Tablas temporales

```sql
-- Tabla temporal (solo existe en la sesion)
CREATE TEMPORARY TABLE temp_resultados AS
SELECT departamento, AVG(salario) as promedio
FROM empleados
GROUP BY departamento;

-- Usar la tabla temporal
SELECT * FROM temp_resultados WHERE promedio > 5000;
```

---

## 8. Transacciones

```sql
BEGIN TRANSACTION;

UPDATE cuentas SET saldo = saldo - 100 WHERE id = 1; -- debitar
UPDATE cuentas SET saldo = saldo + 100 WHERE id = 2; -- acreditar

-- Si todo OK:
COMMIT;

-- Si algo falla:
ROLLBACK;
```

**Propiedades ACID:**
- **A**tomicity: todo o nada
- **C**onsistency: datos validos antes y despues
- **I**solation: transacciones no se interfieren
- **D**urability: cambios persistentes

---

## 9. SQL vs NoSQL

| | SQL (Relacional) | NoSQL |
|---|---|---|
| Estructura | Tablas con esquema fijo | Documentos, clave-valor, grafos |
| Ejemplos | PostgreSQL, MySQL, Oracle | MongoDB, Redis, Cassandra |
| Escalabilidad | Vertical | Horizontal |
| Transacciones | ACID completo | Eventual consistency (BASE) |
| Cuando usar | Datos estructurados, relaciones complejas | Datos flexibles, alta escala, cache |

> **Tip entrevista:** "No es SQL vs NoSQL, es cual se adapta mejor al caso. Datos transaccionales (bancarios, pedidos) → SQL. Cache, sesiones, logs, datos semi-estructurados → NoSQL."

---

## 10. JPA / Hibernate - Queries en Spring Boot

```java
// Repository con metodos derivados (Spring genera el SQL)
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // SELECT * FROM usuarios WHERE edad > ? AND activo = true
    List<Usuario> findByEdadGreaterThanAndActivoTrue(int edad);

    // Query custom con JPQL
    @Query("SELECT u FROM Usuario u WHERE u.departamento.nombre = :depto")
    List<Usuario> findByDepartamento(@Param("depto") String departamento);

    // Query nativa SQL
    @Query(value = "SELECT * FROM usuarios WHERE YEAR(fecha_registro) = :anio",
           nativeQuery = true)
    List<Usuario> findRegistradosEnAnio(@Param("anio") int anio);
}
```

---

## 11. Stored Procedures (SQL Server)

```sql
-- Procedimiento almacenado: bloque de SQL reutilizable guardado en el servidor
-- Ventajas: rendimiento (precompilado), seguridad (permisos), reutilizacion

-- Crear stored procedure con parametros de entrada y salida
CREATE PROCEDURE sp_ObtenerUsuario
    @Email VARCHAR(100),              -- parametro de entrada
    @NombreCompleto VARCHAR(200) OUTPUT  -- parametro de salida
AS
BEGIN
    SELECT @NombreCompleto = nombre + ' ' + apellido
    FROM usuarios
    WHERE email = @Email;

    -- Retornar resultados
    SELECT id, nombre, email, fecha_registro
    FROM usuarios
    WHERE email = @Email;
END;

-- Ejecutar el stored procedure
DECLARE @Nombre VARCHAR(200);
EXEC sp_ObtenerUsuario @Email = 'ana@mail.com', @NombreCompleto = @Nombre OUTPUT;
PRINT @Nombre;  -- "Ana Garcia"

-- Stored procedure con logica de negocio
CREATE PROCEDURE sp_TransferirFondos
    @CuentaOrigen INT,
    @CuentaDestino INT,
    @Monto DECIMAL(10,2)
AS
BEGIN
    BEGIN TRANSACTION;
    BEGIN TRY
        UPDATE cuentas SET saldo = saldo - @Monto WHERE id = @CuentaOrigen;
        UPDATE cuentas SET saldo = saldo + @Monto WHERE id = @CuentaDestino;
        COMMIT;
    END TRY
    BEGIN CATCH
        ROLLBACK;
        THROW;  -- relanzar el error
    END CATCH
END;
```

```java
// Llamar stored procedure desde Spring/JPA
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "EXEC sp_ObtenerUsuario :email, :nombre OUTPUT", nativeQuery = true)
    List<Usuario> obtenerPorProcedimiento(@Param("email") String email);
}

// Con JDBC Template (mas control)
@Repository
public class UsuarioJdbcRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public String obtenerNombre(String email) {
        return jdbc.queryForObject(
            "EXEC sp_ObtenerUsuario ?, ? OUTPUT",
            String.class, email
        );
    }
}
```

---

## 12. EXPLAIN PLAN (optimizacion de queries)

```sql
-- EXPLAIN muestra COMO el motor de BD ejecuta tu query
-- Sirve para detectar queries lentas y optimizarlas

-- PostgreSQL
EXPLAIN ANALYZE SELECT * FROM usuarios WHERE email = 'ana@mail.com';

-- Resultado:
-- Seq Scan on usuarios  (cost=0.00..35.50 rows=1)  ← MALO: escaneo secuencial
-- Filter: (email = 'ana@mail.com')

-- Despues de crear indice:
CREATE INDEX idx_email ON usuarios(email);
EXPLAIN ANALYZE SELECT * FROM usuarios WHERE email = 'ana@mail.com';

-- Resultado:
-- Index Scan using idx_email  (cost=0.00..8.27 rows=1)  ← BIEN: usa indice

-- SQL Server
SET STATISTICS IO ON;
SELECT * FROM usuarios WHERE email = 'ana@mail.com';
-- Muestra: logical reads, physical reads, scan count

-- MySQL
EXPLAIN SELECT * FROM usuarios WHERE email = 'ana@mail.com';
```

```
Que buscar en el EXPLAIN:
- "Seq Scan" / "Table Scan" → escaneo completo (MALO si la tabla es grande)
- "Index Scan" → usa indice (BIEN)
- "Nested Loop" → JOIN eficiente para pocas filas
- "Hash Join" → JOIN eficiente para muchas filas
- "Sort" → ordenamiento (costoso si no hay indice)
- Alto "cost" o "rows" → query potencialmente lenta
```

> **Tip entrevista:** "Uso EXPLAIN PLAN para identificar queries lentas. Si veo un Seq Scan en una tabla grande, creo un indice en las columnas del WHERE o JOIN. Tambien verifico que los indices compuestos sigan el orden correcto."

---

## 13. VARCHAR vs CHAR

```sql
-- CHAR(n): longitud FIJA, rellena con espacios
-- VARCHAR(n): longitud VARIABLE, solo usa lo necesario

CHAR(10)    → 'Hola      '  -- siempre ocupa 10 bytes
VARCHAR(10) → 'Hola'        -- ocupa 4 bytes + overhead

-- Cuando usar cada uno:
-- CHAR:    codigos fijos (DNI, codigo pais, estado 'A'/'I')
-- VARCHAR: textos variables (nombre, email, direccion)

CREATE TABLE usuarios (
    estado CHAR(1),           -- 'A' o 'I' → siempre 1 caracter
    codigo_pais CHAR(3),      -- 'PER', 'COL' → siempre 3 caracteres
    nombre VARCHAR(100),      -- longitud variable
    email VARCHAR(255)        -- longitud variable
);
```

---

## 14. Variables SQL y cursores

```sql
-- VARIABLES en SQL Server
DECLARE @nombre VARCHAR(50) = 'Ana';
DECLARE @edad INT;
SET @edad = 25;

SELECT @nombre = nombre FROM usuarios WHERE id = 1;
PRINT @nombre;

-- Variables en PostgreSQL (dentro de funciones)
DO $$
DECLARE
    v_nombre VARCHAR(50);
    v_total INT := 0;
BEGIN
    SELECT nombre INTO v_nombre FROM usuarios WHERE id = 1;
    SELECT COUNT(*) INTO v_total FROM usuarios;
    RAISE NOTICE 'Nombre: %, Total: %', v_nombre, v_total;
END $$;

-- CURSORES: iterar fila por fila (EVITAR si puedes usar SET-based)
DECLARE @id INT, @nombre VARCHAR(50);
DECLARE cursor_usuarios CURSOR FOR
    SELECT id, nombre FROM usuarios WHERE estado = 'A';

OPEN cursor_usuarios;
FETCH NEXT FROM cursor_usuarios INTO @id, @nombre;
WHILE @@FETCH_STATUS = 0
BEGIN
    PRINT 'Usuario: ' + @nombre;
    FETCH NEXT FROM cursor_usuarios INTO @id, @nombre;
END;
CLOSE cursor_usuarios;
DEALLOCATE cursor_usuarios;
```

> **Tip entrevista:** "Los cursores procesan fila por fila y son MUY lentos comparados con operaciones basadas en conjuntos (SET-based). Solo los uso cuando es absolutamente necesario, como procesar logica condicional compleja por cada fila. Si puedo resolverlo con un UPDATE masivo o un JOIN, eso es siempre mejor."

---

## 15. Schemas y Oracle

```sql
-- SCHEMA: agrupacion logica de objetos dentro de una base de datos
-- Es como una "carpeta" para organizar tablas, vistas, procedures

-- En PostgreSQL/SQL Server:
-- Base de datos → Schema(s) → Tablas
CREATE SCHEMA ventas;
CREATE TABLE ventas.pedidos (id INT, total DECIMAL);
SELECT * FROM ventas.pedidos;

-- En ORACLE es diferente:
-- Un SCHEMA = un USUARIO
-- Cuando creas un usuario en Oracle, automaticamente se crea un schema con su nombre
-- Oracle NO tiene "base de datos" como PostgreSQL → tiene INSTANCIA → SCHEMAS

-- Ejemplo Oracle:
-- Usuario "HR" → schema "HR" → tablas: HR.employees, HR.departments
SELECT * FROM HR.employees;  -- acceder a tabla de otro schema

-- Equivalencia:
-- PostgreSQL: database.schema.tabla
-- Oracle:     instancia.schema(=usuario).tabla
-- SQL Server: server.database.schema.tabla
```

---

## 16. Primary Key y constraints

```sql
-- PRIMARY KEY: identificador unico de cada fila
-- Caracteristicas: unico, no nulo, solo 1 por tabla
CREATE TABLE usuarios (
    id INT PRIMARY KEY IDENTITY(1,1),  -- SQL Server: autoincremental
    -- id SERIAL PRIMARY KEY,           -- PostgreSQL: autoincremental
    dni VARCHAR(10) UNIQUE NOT NULL,    -- UNIQUE: unico pero puede haber varios
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    departamento_id INT,

    -- Foreign Key: referencia a otra tabla
    CONSTRAINT fk_departamento
        FOREIGN KEY (departamento_id) REFERENCES departamentos(id)
);

-- Tipos de constraints:
-- PRIMARY KEY: unico + no nulo (1 por tabla)
-- UNIQUE: unico (puede haber varios por tabla)
-- NOT NULL: obligatorio
-- FOREIGN KEY: referencia a PK de otra tabla
-- CHECK: validacion custom
-- DEFAULT: valor por defecto

ALTER TABLE usuarios ADD CONSTRAINT chk_edad CHECK (edad >= 18);
ALTER TABLE usuarios ALTER COLUMN estado SET DEFAULT 'A';
```

---

## RESPUESTAS RAPIDAS - SQL

| Pregunta | Respuesta |
|----------|-----------|
| INNER vs LEFT JOIN? | INNER: solo coincidencias. LEFT: todo de la izquierda + coincidencias |
| WHERE vs HAVING? | WHERE filtra filas antes de GROUP BY. HAVING filtra despues de agrupar |
| Que es un indice? | Estructura que acelera SELECT pero ralentiza INSERT/UPDATE |
| Que es ACID? | Atomicity, Consistency, Isolation, Durability - propiedades de transacciones |
| SQL vs NoSQL? | SQL para datos estructurados/transaccionales. NoSQL para flexibilidad y escala horizontal |
| DDL vs DML? | DDL define estructura (CREATE, ALTER). DML manipula datos (SELECT, INSERT) |
| @Query en Spring? | Para queries custom cuando los metodos derivados de JPA no alcanzan |
| Que es un Stored Procedure? | Bloque SQL reutilizable guardado en servidor. Precompilado, seguro, reutilizable |
| Que es EXPLAIN PLAN? | Muestra como ejecuta la BD tu query. Detecta si usa indices o escaneo completo |
| VARCHAR vs CHAR? | CHAR: longitud fija (codigos). VARCHAR: longitud variable (textos) |
| Que es un cursor? | Itera fila por fila. Lento, evitar si puedes usar operaciones SET-based |
| Schema en Oracle? | En Oracle, schema = usuario. En PostgreSQL/SQL Server, schema es carpeta logica |
| Que es Primary Key? | Identificador unico de cada fila: unico, no nulo, solo 1 por tabla |

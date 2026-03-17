# MANEJO DE ERRORES Y EXCEPCIONES

---

## 1. try-catch-finally

```java
try {
    int x = 5 / 0;
} catch (ArithmeticException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    // SIEMPRE se ejecuta (haya o no error)
    System.out.println("Limpieza");
}

// Try-with-resources (Java 7+): cierra recursos automaticamente
try (Connection con = DriverManager.getConnection(url, user, pass);
     PreparedStatement ps = con.prepareStatement(sql)) {
    // usar conexion
} catch (SQLException e) {
    log.error("Error BD: {}", e.getMessage());
}
// 'con' y 'ps' se cierran automaticamente aqui
```

> **Tip entrevista:** "Siempre uso try-with-resources para recursos que implementan `AutoCloseable` (conexiones BD, streams, archivos). Evita resource leaks."

---

## 2. Checked vs Unchecked Exceptions

| Tipo | Cuando | Obligatorio manejar? | Ejemplos |
|------|--------|---------------------|----------|
| **Checked** | Errores externos recuperables | Si (try-catch o throws) | `IOException`, `SQLException` |
| **Unchecked** | Errores de programacion | No | `NullPointerException`, `IllegalArgumentException` |
| **Error** | Errores graves del sistema | No (no capturar) | `OutOfMemoryError`, `StackOverflowError` |

**Jerarquia:**
```
Throwable
├── Error (no capturar)
│   ├── OutOfMemoryError
│   └── StackOverflowError
└── Exception
    ├── IOException (checked)
    ├── SQLException (checked)
    └── RuntimeException (unchecked)
        ├── NullPointerException
        ├── IllegalArgumentException
        └── IndexOutOfBoundsException
```

```java
// Checked: el compilador te obliga
try {
    archivo.read();
} catch (IOException e) {
    log.error("No se pudo leer: {}", e.getMessage());
}

// Unchecked: prevenirlos, no capturarlos
String s = null;
// MAL: capturar NullPointerException
// BIEN: validar antes
if (s != null) { s.length(); }
// MEJOR: usar Optional
Optional.ofNullable(s).ifPresent(String::length);
```

---

## 3. Excepciones custom (muy preguntado)

```java
// Excepcion de negocio (unchecked)
public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(Long id) {
        super("Usuario no encontrado con ID: " + id);
    }
}

// Uso en servicio
@Service
public class UsuarioService {
    public UsuarioDTO buscarPorId(Long id) {
        return repository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }
}
```

---

## 4. Manejo global con @ControllerAdvice (Spring Boot)

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorDTO> handleNotFound(UsuarioNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorDTO(404, e.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorDTO> handleDBError(DataAccessException e) {
        log.error("Error de BD: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorDTO(503, "Servicio temporalmente no disponible"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGeneral(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorDTO(500, "Error interno del servidor"));
    }
}

// DTO para respuestas de error consistentes
record ErrorDTO(int codigo, String mensaje) {}
```

> **Tip entrevista:** "En mis proyectos creo excepciones custom por tipo de error de negocio y las manejo centralmente con `@ControllerAdvice`. Esto da respuestas HTTP consistentes y evita try-catch repetidos en cada controller."

---

## RESPUESTAS RAPIDAS - ERRORES

| Pregunta | Respuesta |
|----------|-----------|
| Checked vs Unchecked? | Checked: compilador obliga manejar (IOException). Unchecked: errores de programacion (NullPointer) |
| Que es try-with-resources? | Cierra automaticamente recursos AutoCloseable al salir del try |
| Como manejas excepciones en Spring? | `@ControllerAdvice` + `@ExceptionHandler` para manejo centralizado |
| Creas excepciones custom? | Si, extends RuntimeException para errores de negocio (ej: UsuarioNoEncontrado) |
| finally siempre se ejecuta? | Si, siempre, haya o no excepcion (excepto `System.exit()`) |

# JAVA CORE

---

## 1. Que es JVM?

**JVM (Java Virtual Machine)** es la maquina virtual que ejecuta el bytecode (.class) generado por el compilador `javac`. Es la pieza clave del principio **"Write Once, Run Anywhere"**: tu codigo se compila una sola vez y la JVM de cada SO se encarga de interpretarlo.

**Componentes internos importantes (puntos extra en entrevista):**
- **Class Loader**: carga las clases en memoria
- **Memory Areas**: Heap (objetos), Stack (variables locales y llamadas), Metaspace (metadata de clases)
- **Garbage Collector**: libera memoria automaticamente de objetos sin referencia
- **JIT Compiler**: convierte bytecode a codigo nativo en tiempo de ejecucion para mayor rendimiento

```java
// Compilas: javac Hola.java  → genera Hola.class (bytecode)
// Ejecutas: java Hola        → la JVM interpreta el bytecode
public class Hola {
    public static void main(String[] args) {
        System.out.println("Hola, mundo");
    }
}
```

> **Tip entrevista:** Si te preguntan diferencia entre JDK, JRE y JVM:
> - **JDK** = JRE + herramientas de desarrollo (javac, javadoc)
> - **JRE** = JVM + librerias estandar
> - **JVM** = la maquina virtual que ejecuta el bytecode

---

## 2. Ambito (Scope) de una variable

El scope define **donde una variable es visible y accesible**.

| Tipo | Donde vive | Ciclo de vida |
|------|-----------|---------------|
| **De clase (instancia)** | Dentro de la clase, fuera de metodos | Mientras el objeto exista |
| **Local** | Dentro de un metodo o bloque | Solo durante la ejecucion del metodo |
| **Estatica** | Con `static`, pertenece a la clase | Mientras la clase este cargada |
| **De bloque** | Dentro de un `if`, `for`, `while` | Solo dentro de ese bloque |

```java
public class Ejemplo {
    int atributoClase = 1;       // scope: toda la clase
    static int compartido = 0;   // scope: toda la clase, compartido entre instancias

    public void metodo() {
        int variableLocal = 2;   // scope: solo este metodo
        for (int i = 0; i < 5; i++) {
            // i solo existe dentro del for
        }
        // aqui 'i' ya no existe
    }
}
```

---

## 3. Modificadores de acceso

| Modificador | Misma clase | Mismo paquete | Subclase (otro paquete) | Cualquier clase |
|-------------|:-----------:|:-------------:|:----------------------:|:--------------:|
| `private`   | Si | No | No | No |
| (default)   | Si | Si | No | No |
| `protected` | Si | Si | Si | No |
| `public`    | Si | Si | Si | Si |

```java
public class Persona {
    private String nombre;     // solo esta clase
    String apellido;           // default: mismo paquete
    protected int edad;        // esta clase + subclases
    public String email;       // todos
}
```

> **Tip entrevista:** "private se usa para **encapsulamiento**: los atributos se declaran private y se exponen con getters/setters. Esto protege el estado interno del objeto."

---

## 4. instanceof

Verifica si un objeto es instancia de una clase, subclase o interfaz. Retorna `boolean`. Es util para hacer **casting seguro**.

```java
Object obj = "Hola";
if (obj instanceof String s) {  // Java 16+: pattern matching
    System.out.println(s.toUpperCase()); // HOLA
}

// Caso clasico: verificar antes de castear
Animal animal = new Perro();
if (animal instanceof Perro) {
    Perro p = (Perro) animal;  // casting seguro
}
```

> **Tip entrevista:** Desde Java 16, `instanceof` soporta **pattern matching**: `if (obj instanceof String s)` ya te da la variable casteada directamente.

---

## 5. Maps

Estructura que almacena pares **clave-valor**. Las claves son unicas.

| Implementacion | Orden | Null keys | Thread-safe |
|---------------|-------|-----------|-------------|
| `HashMap` | No garantiza orden | 1 null key | No |
| `LinkedHashMap` | Orden de insercion | 1 null key | No |
| `TreeMap` | Orden natural (sorted) | No permite null key | No |
| `ConcurrentHashMap` | No garantiza orden | No permite null | Si |

```java
Map<String, Integer> edades = new HashMap<>();
edades.put("Ana", 30);
edades.put("Luis", 25);
edades.put("Ana", 31);         // sobrescribe el valor anterior

edades.get("Ana");             // 31
edades.getOrDefault("Pedro", 0); // 0 (no existe, retorna default)
edades.containsKey("Luis");    // true
edades.forEach((k, v) -> System.out.println(k + ": " + v));
```

> **Tip entrevista:** "En produccion uso `ConcurrentHashMap` si hay acceso concurrente. Nunca `Hashtable` porque es legacy y mas lento."

---

## 6. Tipos de datos primitivos vs Wrapper

| Primitivo | Wrapper | Tamano | Rango |
|-----------|---------|--------|-------|
| `byte` | `Byte` | 8 bits | -128 a 127 |
| `short` | `Short` | 16 bits | -32,768 a 32,767 |
| `int` | `Integer` | 32 bits | -2^31 a 2^31-1 |
| `long` | `Long` | 64 bits | -2^63 a 2^63-1 |
| `float` | `Float` | 32 bits | decimales |
| `double` | `Double` | 64 bits | decimales |
| `char` | `Character` | 16 bits | unicode |
| `boolean` | `Boolean` | 1 bit | true/false |

```java
int edad = 25;           // primitivo (stack, mas rapido)
Integer edadObj = 25;    // wrapper (heap, permite null, usado en Collections)

// Autoboxing / Unboxing
Integer a = 5;           // autoboxing: int → Integer
int b = a;               // unboxing: Integer → int
```

> **Tip entrevista:** "Los primitivos no pueden ser null y viven en el stack, son mas eficientes. Los wrappers se usan en Collections porque los generics no aceptan primitivos: `List<int>` no compila, necesitas `List<Integer>`."

---

## 7. Instanciar un objeto

Crear un objeto es reservar memoria en el **Heap** usando `new`. La variable referencia vive en el **Stack**.

```java
public class Persona {
    String nombre;

    public Persona(String nombre) {
        this.nombre = nombre;
    }
}

Persona p = new Persona("Carlos");
// p → referencia en Stack
// el objeto Persona → vive en Heap
```

> **Tip entrevista:** Si te preguntan "que pasa internamente con `new`": 1) Se reserva memoria en Heap, 2) Se llama al constructor, 3) Se retorna la referencia al objeto.

---

## 8. Hilos (Threads)

Un hilo es una unidad de ejecucion dentro de un proceso. Java soporta **multithreading**.

**3 formas de crear hilos:**

```java
// 1. Extendiendo Thread
class MiHilo extends Thread {
    public void run() { System.out.println("Hilo 1"); }
}
new MiHilo().start();

// 2. Implementando Runnable (RECOMENDADO - permite herencia multiple)
Runnable tarea = () -> System.out.println("Hilo 2");
new Thread(tarea).start();

// 3. ExecutorService (MEJOR PRACTICA en produccion)
ExecutorService exec = Executors.newFixedThreadPool(2);
exec.submit(() -> System.out.println("Hilo con Executor"));
exec.shutdown(); // siempre cerrar el pool
```

> **Tip entrevista:** "En produccion nunca creo hilos manualmente con `new Thread()`. Uso `ExecutorService` porque gestiona un pool de hilos, es mas eficiente y evita crear hilos infinitos. Para tareas asincronas uso `CompletableFuture`."

---

## 9. Versiones de Java: 7 vs 8 vs 11 vs 17

| Version | Caracteristicas Clave |
|---------|----------------------|
| **Java 7** | Try-with-resources, diamond operator `<>`, switch con String |
| **Java 8** | Lambdas, Streams, Optional, `default` methods en interfaces, `LocalDate` |
| **Java 11** | `var`, HttpClient, `String.isBlank()`, `List.of()`, `Files.readString()` |
| **Java 17** | Sealed classes, pattern matching, records, text blocks, switch expressions |

```java
// Java 7: try-with-resources
try (FileReader fr = new FileReader("f.txt")) { /* ... */ }

// Java 8: lambdas + streams
list.stream().filter(x -> x > 5).collect(Collectors.toList());

// Java 11: var
var list = List.of("A", "B");

// Java 17: records (DTO inmutable en 1 linea)
record PersonaDTO(String nombre, int edad) {}
```

> **Tip entrevista:** "En mi ultimo proyecto usamos Java 17 con records para DTOs y sealed classes para modelar estados. Si el proyecto es Java 8, conozco bien Streams, Optional y lambdas."

---

## 10. Collections: List, Set, Map

| Tipo | Duplicados | Orden | Acceso | Cuando usar |
|------|:----------:|:-----:|--------|------------|
| `ArrayList` | Si | Insercion | Por indice O(1) | Lecturas frecuentes |
| `LinkedList` | Si | Insercion | Secuencial O(n) | Inserciones/eliminaciones frecuentes |
| `HashSet` | No | No garantiza | contains O(1) | Buscar si existe un elemento |
| `LinkedHashSet` | No | Insercion | contains O(1) | Set con orden de insercion |
| `TreeSet` | No | Natural (sorted) | contains O(log n) | Set ordenado |
| `HashMap` | Keys unicas | No garantiza | get O(1) | Clave-valor rapido |
| `TreeMap` | Keys unicas | Natural | get O(log n) | Clave-valor ordenado |

```java
// List: permite duplicados, acceso por indice
List<String> lista = new ArrayList<>();
lista.add("A"); lista.add("B"); lista.add("A"); // [A, B, A]

// Set: NO permite duplicados
Set<String> set = new HashSet<>();
set.add("A"); set.add("B"); set.add("A"); // [A, B] (sin duplicado)

// Map: pares clave-valor, claves unicas
Map<Integer, String> mapa = new HashMap<>();
mapa.put(1, "Uno"); mapa.put(2, "Dos");
mapa.put(1, "Nuevo Uno"); // sobrescribe clave 1
```

> **Tip entrevista:** "Elijo la coleccion segun el caso: `ArrayList` para listas ordenadas, `HashSet` para unicidad rapida, `HashMap` para lookups por clave. Si hay concurrencia, uso `ConcurrentHashMap` o `CopyOnWriteArrayList`."

---

## 11. Estructura de una clase Java

Una clase Java solo puede contener: **propiedades (atributos)** y **metodos**. Todo lo demas (constructores, bloques static, inner classes) son variaciones de estos dos.

```java
public class Producto {
    // PROPIEDADES (estado del objeto)
    private Long id;
    private String nombre;
    private double precio;
    private static int contador = 0;    // propiedad estatica (de clase)

    // CONSTRUCTOR (metodo especial para inicializar)
    public Producto(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
        contador++;
    }

    // METODOS (comportamiento)
    public double calcularDescuento(double porcentaje) {
        return precio * (1 - porcentaje / 100);
    }

    // Getters y Setters (metodos de acceso)
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Metodo estatico (pertenece a la clase, no a la instancia)
    public static int getContador() { return contador; }
}
```

> **Tip entrevista:** "Una clase Java solo tiene dos cosas: propiedades (datos) y metodos (comportamiento). Los constructores son metodos especiales. Los getters/setters son metodos de acceso. Todo lo que hay en una clase cae en una de estas dos categorias."

---

## 12. Hilos virtuales (Virtual Threads - Java 21)

Java 21 introduce **Virtual Threads** (Project Loom): hilos ultra-ligeros gestionados por la JVM, no por el SO.

```java
// Hilo tradicional (Platform Thread)
// - 1 thread Java = 1 thread del SO
// - Pesado: ~1MB de stack por thread
// - Limite practico: ~10,000 threads

// Virtual Thread (Java 21+)
// - Gestionado por la JVM, no por el SO
// - Ultra ligero: ~1KB
// - Puede crear MILLONES de virtual threads

// Crear virtual thread
Thread.startVirtualThread(() -> {
    System.out.println("Hilo virtual: " + Thread.currentThread());
});

// ExecutorService con virtual threads
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 100_000; i++) {
        executor.submit(() -> {
            // Cada tarea corre en su propio virtual thread
            Thread.sleep(Duration.ofSeconds(1));
            return "resultado";
        });
    }
}
// 100,000 tareas concurrentes - imposible con platform threads!

// Spring Boot 3.2+ con virtual threads
// application.yml
spring:
  threads:
    virtual:
      enabled: true   # Tomcat usa virtual threads para requests
```

> **Tip entrevista:** "Los virtual threads resuelven el problema de escalabilidad de los platform threads. Con threads tradicionales, un servidor Tomcat maneja ~200 requests concurrentes. Con virtual threads, puede manejar miles porque no bloquean threads del SO."

---

## 13. Bloqueante vs No Bloqueante vs Reactivo

```
BLOQUEANTE (sincrono):
  Thread-1: [Request] ──espera BD──────── [Response] → thread ocupado esperando
  Thread-2: [Request] ──espera API──────── [Response] → thread ocupado esperando
  Problema: 200 threads = 200 requests simultaneos maximo

NO BLOQUEANTE (virtual threads - Java 21):
  Virtual-Thread-1: [Request] ──espera BD── [libera thread] ──[continua]── [Response]
  Ventaja: mismo modelo de codigo sincrono pero sin bloquear threads del SO
  Ideal: aplicaciones tradicionales que quieren escalar sin reescribir

REACTIVO (Spring WebFlux):
  Event-Loop: [Request-1]──callback──[Request-2]──callback──[Response-1]──[Response-2]
  Ventaja: un solo thread maneja muchos requests via callbacks
  Retorna: Mono<T> / Flux<T> en lugar de T
  Desventaja: codigo mas complejo, debugging dificil
```

```java
// BLOQUEANTE (Spring MVC clasico)
@GetMapping("/{id}")
public UsuarioDTO obtener(@PathVariable Long id) {
    return service.buscarPorId(id);  // el thread espera hasta que la BD responda
}

// NO BLOQUEANTE con Virtual Threads (Spring MVC + Java 21)
// Mismo codigo! Solo activas virtual threads en config
@GetMapping("/{id}")
public UsuarioDTO obtener(@PathVariable Long id) {
    return service.buscarPorId(id);  // virtual thread se suspende, no bloquea thread del SO
}

// REACTIVO (Spring WebFlux)
@GetMapping("/{id}")
public Mono<UsuarioDTO> obtener(@PathVariable Long id) {
    return service.buscarPorId(id);  // retorna Mono, se procesa cuando hay resultado
}
```

> **Tip entrevista:** "Para proyectos nuevos con Java 21, recomiendo virtual threads sobre WebFlux. Obtienes la misma escalabilidad pero con codigo sincrono mas simple. WebFlux sigue siendo mejor para streaming o event-driven puro."

---

## RESPUESTAS RAPIDAS - JAVA CORE

| Pregunta | Respuesta |
|----------|-----------|
| Que es JVM? | Maquina virtual que ejecuta bytecode, permite portabilidad entre SOs |
| JDK vs JRE vs JVM? | JDK = JRE + herramientas dev. JRE = JVM + libs. JVM = ejecuta bytecode |
| Diferencia primitivo vs wrapper? | Primitivo: stack, no null, rapido. Wrapper: heap, null, para Collections |
| Que es autoboxing? | Conversion automatica entre primitivo y wrapper (`int` ↔ `Integer`) |
| HashMap vs ConcurrentHashMap? | HashMap no es thread-safe, ConcurrentHashMap si |
| List vs Set vs Map? | List: ordenada con duplicados. Set: sin duplicados. Map: clave-valor |
| Que es instanceof? | Verifica si un objeto es instancia de una clase o subclase |
| Que tiene una clase Java? | Solo propiedades (datos) y metodos (comportamiento). Constructores son metodos especiales |
| Virtual threads? | Hilos ultra-ligeros de Java 21, gestionados por la JVM. Millones concurrentes |
| Bloqueante vs reactivo? | Bloqueante: 1 thread espera. Reactivo: event loop con Mono/Flux. Virtual threads: mejor de ambos |

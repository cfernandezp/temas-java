# JAVA 8+ - Programacion Funcional y Streams

---

## 1. Programacion Funcional (Java 8)

**Interfaces funcionales principales:**

| Interface | Entrada | Salida | Uso |
|-----------|---------|--------|-----|
| `Predicate<T>` | T | boolean | Filtrar |
| `Function<T,R>` | T | R | Transformar |
| `Consumer<T>` | T | void | Ejecutar accion |
| `Supplier<T>` | nada | T | Generar/proveer |
| `BiFunction<T,U,R>` | T, U | R | Transformar con 2 entradas |
| `UnaryOperator<T>` | T | T | Transformar mismo tipo |

```java
// Lambda = implementacion corta de interfaz funcional
Predicate<String> esLargo = s -> s.length() > 5;
Function<String, Integer> longitud = String::length;  // method reference
Consumer<String> imprimir = System.out::println;
Supplier<String> saludo = () -> "Hola";

// Componer funciones
Function<String, String> mayusculas = String::toUpperCase;
Function<String, String> agregarSigno = s -> s + "!";
Function<String, String> gritando = mayusculas.andThen(agregarSigno);
gritando.apply("hola"); // "HOLA!"
```

---

## 2. Optional

Contenedor que puede o no tener un valor. **Evita NullPointerException**.

```java
// CREAR
Optional<String> conValor = Optional.of("Hola");
Optional<String> nullable = Optional.ofNullable(getNombre()); // puede ser null
Optional<String> vacio = Optional.empty();

// USAR
String resultado = nullable.orElse("Desconocido");          // valor por defecto
String resultado2 = nullable.orElseGet(() -> calcularDefault()); // lazy
String resultado3 = nullable.orElseThrow(() -> new NotFoundException("No existe"));

// ENCADENAR
Optional<String> email = usuario
    .map(Usuario::getDireccion)
    .map(Direccion::getEmail)
    .filter(e -> e.contains("@"));
```

> **Tip entrevista:** "Nunca uso `optional.get()` sin verificar, eso lanza NoSuchElementException. Siempre uso `orElse`, `orElseThrow` o `ifPresent`. Optional no debe usarse como parametro de metodo ni como atributo de clase, solo como **retorno**."

---

## 3. Stream API

Stream es una **pipeline de operaciones** sobre colecciones. **No modifica** la coleccion original.

**Operaciones intermedias** (retornan Stream, son lazy):
`filter`, `map`, `flatMap`, `sorted`, `distinct`, `peek`, `limit`, `skip`

**Operaciones terminales** (ejecutan la pipeline):
`collect`, `forEach`, `count`, `reduce`, `findFirst`, `findAny`, `anyMatch`, `allMatch`

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 3, 2);

// Filtrar, transformar, recoger
List<Integer> pares = numeros.stream()
    .filter(n -> n % 2 == 0)
    .distinct()                    // elimina duplicados
    .sorted()                      // ordena
    .collect(Collectors.toList()); // [2, 4]

// Suma
int suma = numeros.stream().mapToInt(Integer::intValue).sum(); // 20

// Conteo
long mayoresA3 = numeros.stream().filter(n -> n > 3).count(); // 2

// Reduce (acumular)
int producto = numeros.stream().reduce(1, (a, b) -> a * b);

// findFirst
Optional<Integer> primero = numeros.stream()
    .filter(n -> n > 3)
    .findFirst(); // Optional[4]
```

---

## 4. Streams - Operaciones avanzadas (frecuentes en entrevistas)

```java
List<Empleado> empleados = getEmpleados();

// AGRUPAR POR departamento
Map<String, List<Empleado>> porDepto = empleados.stream()
    .collect(Collectors.groupingBy(Empleado::getDepartamento));

// AGRUPAR Y CONTAR
Map<String, Long> conteoDepto = empleados.stream()
    .collect(Collectors.groupingBy(Empleado::getDepartamento, Collectors.counting()));

// AGRUPAR Y SUMAR salarios
Map<String, Double> sumaDepto = empleados.stream()
    .collect(Collectors.groupingBy(
        Empleado::getDepartamento,
        Collectors.summingDouble(Empleado::getSalario)
    ));

// flatMap: aplanar listas anidadas
List<List<String>> listas = List.of(List.of("A","B"), List.of("C","D"));
List<String> plana = listas.stream()
    .flatMap(Collection::stream)
    .collect(Collectors.toList()); // [A, B, C, D]

// Convertir List a Map
Map<Long, String> mapaEmpleados = empleados.stream()
    .collect(Collectors.toMap(Empleado::getId, Empleado::getNombre));

// Joining (concatenar strings)
String nombres = empleados.stream()
    .map(Empleado::getNombre)
    .collect(Collectors.joining(", ")); // "Ana, Luis, Carlos"

// parallelStream (procesamiento paralelo)
long total = numeros.parallelStream().filter(n -> n > 2).count();
```

> **Tip entrevista:** Preguntas clasicas:
> - "Filtra empleados con salario > 5000 y agrupa por departamento" → `filter` + `groupingBy`
> - "Obtener lista de nombres unicos en mayuscula" → `map` + `distinct` + `toUpperCase`
> - "Aplanar lista de listas" → `flatMap`

---

## 5. Manejo de Ficheros (NIO)

```java
// Escribir
Files.write(Paths.get("saludo.txt"), "Hola".getBytes());
Files.writeString(Path.of("saludo.txt"), "Hola mundo"); // Java 11+

// Leer todo
String contenido = Files.readString(Path.of("saludo.txt")); // Java 11+
List<String> lineas = Files.readAllLines(Paths.get("saludo.txt"));

// Leer con Stream (eficiente para archivos grandes)
try (Stream<String> stream = Files.lines(Paths.get("log.txt"))) {
    stream.filter(line -> line.contains("ERROR"))
          .forEach(System.out::println);
}
```

---

## RESPUESTAS RAPIDAS - FUNCIONAL & STREAMS

| Pregunta | Respuesta |
|----------|-----------|
| Que es una lambda? | Implementacion corta de una interfaz funcional (1 metodo abstracto) |
| Que es Stream? | Pipeline de operaciones funcionales sobre colecciones, sin modificar la original |
| filter vs map? | filter selecciona elementos, map transforma cada elemento |
| flatMap para que? | Aplanar estructuras anidadas (List de Lists → List plana) |
| Que es Optional? | Contenedor que puede o no tener valor, evita NullPointerException |
| collect vs forEach? | collect acumula resultado (List, Map), forEach ejecuta accion sin retorno |
| Que es Predicate? | Interfaz funcional que recibe T y retorna boolean (para filtrar) |
| parallelStream? | Procesa el stream en multiples hilos, util para grandes volumenes de datos |

# POO - Programacion Orientada a Objetos

---

## 1. Que es POO?

Es un paradigma que organiza el codigo en **clases** (moldes) y **objetos** (instancias). Se basa en **4 pilares**:

| Pilar | Que es | Ejemplo rapido |
|-------|--------|---------------|
| **Encapsulamiento** | Ocultar el estado interno, exponer solo lo necesario | `private` + getters/setters |
| **Herencia** | Reutilizar codigo de una clase padre | `class Perro extends Animal` |
| **Polimorfismo** | Mismo metodo, diferente comportamiento segun el objeto | `animal.sonido()` → "Guau" o "Miau" |
| **Abstraccion** | Simplificar la complejidad, mostrar solo lo esencial | `abstract class`, `interface` |

```java
// Los 4 pilares en accion:
abstract class Animal {                    // ABSTRACCION
    private String nombre;                 // ENCAPSULAMIENTO
    public String getNombre() { return nombre; }
    abstract void sonido();
}

class Perro extends Animal {               // HERENCIA
    void sonido() { System.out.println("Guau"); } // POLIMORFISMO
}

class Gato extends Animal {
    void sonido() { System.out.println("Miau"); } // POLIMORFISMO
}

Animal a = new Perro();  // referencia Animal, objeto Perro
a.sonido();              // "Guau" → polimorfismo en accion
```

---

## 2. Clases Abstractas vs Interfaces

| Caracteristica | Clase Abstracta | Interface |
|---------------|----------------|-----------|
| Instanciar | No | No |
| Metodos con cuerpo | Si | Si (desde Java 8 con `default`) |
| Atributos con estado | Si | Solo constantes (`static final`) |
| Constructor | Si | No |
| Herencia multiple | No (1 sola) | Si (multiples interfaces) |

```java
// Clase abstracta: cuando hay logica compartida
abstract class Animal {
    String nombre; // puede tener estado
    abstract void sonido();
    void dormir() { System.out.println("Zzz..."); } // metodo concreto
}

// Interface: cuando defines un contrato
interface Volador {
    void volar(); // implicitamente public abstract
    default void aterrizar() { System.out.println("Aterrizando..."); }
}
```

> **Tip entrevista:** "Uso clase abstracta cuando hay **estado compartido** o **logica comun** entre subclases. Uso interface cuando quiero definir un **contrato** que diferentes clases pueden implementar, especialmente si necesitan **herencia multiple**."

---

## 3. Clases Anonimas

Clases sin nombre que se definen e instancian en el momento. Utiles para implementaciones rapidas de interfaces.

```java
// Clase anonima
Runnable r = new Runnable() {
    public void run() {
        System.out.println("Clase anonima ejecutada");
    }
};

// Lo mismo con lambda (Java 8+, mas limpio)
Runnable r2 = () -> System.out.println("Lambda ejecutada");

new Thread(r2).start();
```

> **Tip entrevista:** "Desde Java 8, las clases anonimas para **interfaces funcionales** (1 solo metodo abstracto) se reemplazan por lambdas. Son mas legibles y concisas."

---

## 4. Sobrecarga vs Sobreescritura

| | Sobrecarga (Overloading) | Sobreescritura (Overriding) |
|---|---|---|
| **Que es** | Mismo nombre, diferentes parametros | Redefinir metodo del padre en el hijo |
| **Donde** | Misma clase | Clase hija |
| **Resolucion** | En compilacion | En ejecucion (polimorfismo) |
| **Return type** | Puede cambiar | Debe ser igual o covariante |

```java
// SOBRECARGA: mismo nombre, distintos parametros
class Calculadora {
    int suma(int a, int b) { return a + b; }
    double suma(double a, double b) { return a + b; }
    int suma(int a, int b, int c) { return a + b + c; }
}

// SOBREESCRITURA: redefinir metodo del padre
class Animal {
    void sonido() { System.out.println("..."); }
}
class Perro extends Animal {
    @Override  // siempre usar esta anotacion
    void sonido() { System.out.println("Guau"); }
}
```

---

## 5. Polimorfismo

Un objeto puede comportarse de diferentes formas segun su tipo real. La referencia es del tipo padre, pero el metodo ejecutado es del tipo hijo.

```java
class Animal {
    void sonido() { System.out.println("Sonido generico"); }
}
class Gato extends Animal {
    @Override
    void sonido() { System.out.println("Miau"); }
}

Animal a = new Gato();   // tipo referencia: Animal, tipo real: Gato
a.sonido();              // "Miau" → se ejecuta el metodo del tipo REAL
```

> **Tip entrevista:** "El polimorfismo permite escribir codigo generico. Por ejemplo, un metodo que recibe `List<Animal>` puede procesar Perros, Gatos, etc. sin saber el tipo especifico. Esto respeta el principio Open/Closed de SOLID."

---

## 6. DTO y DAO

| Patron | Responsabilidad | Capa |
|--------|----------------|------|
| **DTO** (Data Transfer Object) | Transportar datos entre capas | Controller ↔ Service |
| **DAO** (Data Access Object) | Acceso a base de datos (CRUD) | Service ↔ Database |

```java
// DTO: solo datos, sin logica de negocio
public class UsuarioDTO {
    private String nombre;
    private int edad;
    // getters y setters
}

// Java 17: record reemplaza DTOs simples
record UsuarioDTO(String nombre, int edad) {}

// DAO: maneja la persistencia
public class UsuarioDAO {
    public void guardar(UsuarioDTO u) {
        // INSERT INTO usuarios ...
    }
    public UsuarioDTO buscarPorId(Long id) {
        // SELECT * FROM usuarios WHERE id = ?
        return new UsuarioDTO("Carlos", 25);
    }
}
```

> **Tip entrevista:** "En Spring Boot, el DAO se reemplaza por **Repository** (`JpaRepository`). El DTO es importante para **no exponer la entidad** directamente al cliente, por seguridad y desacoplamiento."

---

## 7. extends, interface, implements

```java
// EXTENDS: herencia de clase (solo 1 padre)
class Animal {
    void hablar() { System.out.println("Animal habla"); }
}
class Perro extends Animal {} // hereda hablar()

// INTERFACE + IMPLEMENTS: contrato (multiples interfaces)
interface Volador { void volar(); }
interface Nadador { void nadar(); }

class Pato extends Animal implements Volador, Nadador {
    public void volar() { System.out.println("Volando..."); }
    public void nadar() { System.out.println("Nadando..."); }
}
```

> **Tip entrevista:** "Java no permite herencia multiple de clases (evita el diamond problem), pero si permite implementar **multiples interfaces**. Por eso se dice: 'Programa hacia interfaces, no hacia implementaciones'."

---

## RESPUESTAS RAPIDAS - POO

| Pregunta | Respuesta |
|----------|-----------|
| 4 pilares de POO? | Encapsulamiento, Herencia, Polimorfismo, Abstraccion |
| Diferencia abstract vs interface? | Abstract tiene estado y logica compartida; interface es contrato puro y permite herencia multiple |
| Que es polimorfismo? | Mismo metodo, diferente comportamiento segun el objeto real en tiempo de ejecucion |
| Overloading vs Overriding? | Overloading: mismo nombre, distintos params (compilacion). Overriding: redefinir metodo del padre (ejecucion) |
| Que es DTO? | Objeto para transportar datos entre capas, sin logica de negocio |
| Que es DAO? | Objeto que maneja acceso a la base de datos (CRUD) |
| extends vs implements? | extends hereda de 1 clase; implements implementa N interfaces |

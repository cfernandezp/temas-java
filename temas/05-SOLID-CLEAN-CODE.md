# SOLID y CLEAN CODE

---

## SOLID

### S - Single Responsibility (SRP)
**Una clase = una razon para cambiar.**

```java
// MAL: 2 responsabilidades
class Reporte {
    public String generarPDF() { return "PDF"; }
    public void guardarEnBD(String data) { /* BD */ }
}

// BIEN: separado
class GeneradorReporte { public String generarPDF() { return "PDF"; } }
class RepositorioReporte { public void guardar(String data) { /* BD */ } }
```

> **En Spring Boot:** Cada `@Service` debe tener una sola responsabilidad. No mezclar logica de negocio con acceso a datos.

---

### O - Open/Closed (OCP)
**Abierto para extension, cerrado para modificacion.** Agregar comportamiento sin tocar codigo existente.

```java
interface Descuento { double aplicar(double precio); }

class DescuentoNavidad implements Descuento {
    public double aplicar(double precio) { return precio * 0.8; }
}

// Agregar nuevo descuento SIN modificar nada existente:
class DescuentoBlackFriday implements Descuento {
    public double aplicar(double precio) { return precio * 0.5; }
}

class Carrito {
    public double pagar(double precio, Descuento descuento) {
        return descuento.aplicar(precio); // acepta cualquier descuento nuevo
    }
}
```

> **En Spring Boot:** Crear nueva implementacion de interface + `@Service` sin tocar las existentes.

---

### L - Liskov Substitution (LSP)
**Una subclase debe poder usarse donde se usa su padre sin romper nada.**

```java
// MAL: Pinguino no puede volar, rompe el contrato
class Ave { public void volar() { /* ... */ } }
class Pinguino extends Ave {
    public void volar() { throw new UnsupportedOperationException(); } // ROMPE LSP
}

// BIEN: separar capacidades
interface Volador { void volar(); }
class Gaviota implements Volador { public void volar() { /* ... */ } }
class Pinguino { public void nadar() { /* ... */ } }
```

> **Regla practica:** Si en tu subclase necesitas lanzar `UnsupportedOperationException`, probablemente estas violando LSP.

---

### I - Interface Segregation (ISP)
**No forzar a implementar metodos que no se usan.** Interfaces pequenas y especificas.

```java
// MAL: interfaz gorda
interface Trabajador { void programar(); void disenar(); void testear(); }

// BIEN: interfaces especificas
interface Programador { void programar(); }
interface Disenador { void disenar(); }
interface Tester { void testear(); }

class Dev implements Programador {
    public void programar() { System.out.println("Programando..."); }
}

class FullStack implements Programador, Disenador {
    public void programar() { /* ... */ }
    public void disenar() { /* ... */ }
}
```

---

### D - Dependency Inversion (DIP)
**Depender de abstracciones, no de clases concretas.** Usar inyeccion de dependencias.

```java
// MAL: acoplado a implementacion concreta
class Servicio {
    private MySQLRepository repo = new MySQLRepository(); // acoplado!
}

// BIEN: depende de abstraccion
interface Repository { void guardar(Object o); }

class Servicio {
    private final Repository repo;
    public Servicio(Repository repo) { this.repo = repo; } // inyectado
}

// En Spring Boot:
@Service
class UsuarioService {
    private final UsuarioRepository repo; // interface
    public UsuarioService(UsuarioRepository repo) { this.repo = repo; }
    // Spring inyecta la implementacion automaticamente
}
```

> **Tip entrevista:** "SOLID no es solo teoria. En Spring Boot se aplica naturalmente: `@Service` (SRP), interfaces + multiples implementaciones (OCP, DIP), interfaces pequenas como `Repository` (ISP). Es la base de un codigo mantenible."

---

## CLEAN CODE (Robert C. Martin)

| Principio | Ejemplo malo | Ejemplo bueno |
|-----------|-------------|---------------|
| Nombres claros | `int d;` `String s;` | `int diasDesdeCreacion;` `String nombreCompleto;` |
| Metodos pequenos | Metodo de 100 lineas | Metodo de 10-15 lineas max |
| Sin comentarios obvios | `i++; // incrementa i` | El codigo se explica solo |
| Sin duplicacion (DRY) | Copiar/pegar logica | Extraer a un metodo reutilizable |
| Un nivel de abstraccion | Mezclar logica de negocio con SQL | Separar en capas (Service, Repository) |
| Metodos sin side effects | Metodo que modifica estado global | Metodos puros que retornan resultado |
| Parametros: max 3 | `crear(a, b, c, d, e, f)` | Agrupar en un objeto: `crear(ConfigDTO config)` |

```java
// MAL
public double calc(double a, int t) {
    double r = a;
    for (int i = 0; i < t; i++) {
        r = r * 1.05;
    }
    return r;
}

// BIEN
public double calcularInteresCompuesto(double montoInicial, int anios) {
    double montoFinal = montoInicial;
    for (int anio = 0; anio < anios; anio++) {
        montoFinal *= TASA_INTERES_ANUAL;
    }
    return montoFinal;
}
```

> **Tip entrevista:** "Clean Code para mi es: si otro dev lee mi codigo en 6 meses, lo entiende sin preguntarme. Nombres descriptivos, metodos cortos con una sola responsabilidad, y tests que documentan el comportamiento."

---

## RESPUESTAS RAPIDAS - SOLID

| Pregunta | Respuesta |
|----------|-----------|
| Que es SOLID? | 5 principios de diseno OO para codigo mantenible y extensible |
| SRP? | Una clase, una responsabilidad, una razon para cambiar |
| OCP? | Abierto para extension (nuevas clases), cerrado para modificacion (no tocar existentes) |
| LSP? | Subclase debe poder reemplazar al padre sin romper nada |
| ISP? | Interfaces pequenas y especificas, no forzar metodos innecesarios |
| DIP? | Depender de abstracciones (interfaces), no de implementaciones concretas |
| Clean Code? | Nombres claros, metodos cortos, sin duplicacion, codigo que se explica solo |

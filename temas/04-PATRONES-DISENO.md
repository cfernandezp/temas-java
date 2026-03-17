# PATRONES DE DISENO

---

## Resumen rapido

| Tipo | Patron | Una frase |
|------|--------|-----------|
| Creacional | **Singleton** | Una sola instancia global |
| Creacional | **Factory** | Crear objetos sin exponer la logica |
| Creacional | **Builder** | Construir objetos complejos paso a paso |
| Estructural | **Adapter** | Hacer compatible una interfaz incompatible |
| Estructural | **Decorator** | Agregar funcionalidad sin modificar la clase |
| Comportamiento | **Strategy** | Cambiar algoritmo en runtime |
| Comportamiento | **Observer** | Notificar a otros cuando cambia un estado |

---

## 1. CREACIONALES

### Singleton
Solo una instancia global. Usar para: conexiones BD, configuracion, caches.

```java
// Version thread-safe (double-check locking)
public class ConexionDB {
    private static volatile ConexionDB instancia;

    private ConexionDB() {} // constructor privado

    public static ConexionDB getInstancia() {
        if (instancia == null) {
            synchronized (ConexionDB.class) {
                if (instancia == null) {
                    instancia = new ConexionDB();
                }
            }
        }
        return instancia;
    }
}

// En Spring Boot: @Service, @Component, @Repository son Singleton por defecto
// No necesitas implementarlo manualmente
```

### Factory Method
Crea objetos sin exponer la logica de instanciacion al cliente. Evita `if/else` o `switch` para crear objetos.

```java
interface Notificacion { void enviar(String mensaje); }

class NotificacionEmail implements Notificacion {
    public void enviar(String msg) { System.out.println("Email: " + msg); }
}
class NotificacionSMS implements Notificacion {
    public void enviar(String msg) { System.out.println("SMS: " + msg); }
}

class NotificacionFactory {
    public static Notificacion crear(String tipo) {
        return switch (tipo) {
            case "email" -> new NotificacionEmail();
            case "sms"   -> new NotificacionSMS();
            default -> throw new IllegalArgumentException("Tipo no soportado");
        };
    }
}

// Uso
Notificacion n = NotificacionFactory.crear("email");
n.enviar("Hola!");
```

### Builder (bonus - muy preguntado)
Construir objetos complejos paso a paso. Evita constructores con muchos parametros.

```java
public class Usuario {
    private String nombre;
    private String email;
    private int edad;

    private Usuario() {}

    public static class Builder {
        private Usuario u = new Usuario();
        public Builder nombre(String nombre) { u.nombre = nombre; return this; }
        public Builder email(String email) { u.email = email; return this; }
        public Builder edad(int edad) { u.edad = edad; return this; }
        public Usuario build() { return u; }
    }
}

// Uso (fluido y legible)
Usuario user = new Usuario.Builder()
    .nombre("Carlos")
    .email("carlos@mail.com")
    .edad(25)
    .build();

// En la practica: Lombok @Builder hace esto automatico
@Builder
public class Usuario {
    private String nombre;
    private String email;
    private int edad;
}
```

---

## 2. ESTRUCTURALES

### Adapter
Hace compatible una interfaz que no lo es. Como un adaptador de enchufe.

```java
// Sistema viejo que no puedes modificar
class SistemaLegacy {
    public String obtenerXML() { return "<data>info</data>"; }
}

// Tu sistema necesita JSON
interface ServicioModerno { String obtenerJSON(); }

// Adapter: traduce XML a JSON
class AdaptadorLegacy implements ServicioModerno {
    private SistemaLegacy legacy;

    public AdaptadorLegacy(SistemaLegacy legacy) { this.legacy = legacy; }

    public String obtenerJSON() {
        String xml = legacy.obtenerXML();
        return convertirXmlAJson(xml);
    }
}
```

> **Tip entrevista:** "Un ejemplo real: cuando migras un sistema legacy, el Adapter permite que el codigo nuevo use el viejo sin modificarlo. Tambien lo uso para integrar librerias de terceros."

### Decorator
Agrega funcionalidad a un objeto sin modificar su clase. Se apilan como capas.

```java
interface Notificador { void enviar(String mensaje); }

class NotificadorBase implements Notificador {
    public void enviar(String mensaje) { System.out.println("App: " + mensaje); }
}

class ConEmail implements Notificador {
    private Notificador decorado;
    public ConEmail(Notificador decorado) { this.decorado = decorado; }
    public void enviar(String mensaje) {
        decorado.enviar(mensaje);
        System.out.println("+ Email: " + mensaje);
    }
}

class ConSMS implements Notificador {
    private Notificador decorado;
    public ConSMS(Notificador decorado) { this.decorado = decorado; }
    public void enviar(String mensaje) {
        decorado.enviar(mensaje);
        System.out.println("+ SMS: " + mensaje);
    }
}

// Uso: apilar decoradores
Notificador n = new ConSMS(new ConEmail(new NotificadorBase()));
n.enviar("Alerta"); // App: Alerta + Email: Alerta + SMS: Alerta
```

> **Tip entrevista:** "Ejemplo real: `BufferedReader(new FileReader(file))` en Java IO es Decorator. En Spring, los filtros de seguridad tambien siguen este patron."

---

## 3. COMPORTAMIENTO

### Strategy
Cambiar algoritmo en tiempo de ejecucion. Elimina if/else largos.

```java
interface EstrategiaPago { void pagar(double cantidad); }

class PagoTarjeta implements EstrategiaPago {
    public void pagar(double cantidad) { System.out.println("Tarjeta: " + cantidad); }
}
class PagoPayPal implements EstrategiaPago {
    public void pagar(double cantidad) { System.out.println("PayPal: " + cantidad); }
}

class Carrito {
    private EstrategiaPago estrategia;
    public void setEstrategia(EstrategiaPago e) { this.estrategia = e; }
    public void cobrar(double monto) { estrategia.pagar(monto); }
}

// Uso
Carrito c = new Carrito();
c.setEstrategia(new PagoTarjeta());
c.cobrar(100.0);
```

> **Tip entrevista:** "En Spring Boot, Strategy se implementa elegantemente con inyeccion de dependencias:
> ```java
> @Service
> class PagoService {
>     private final Map<String, EstrategiaPago> estrategias;
>     // Spring inyecta TODAS las implementaciones automaticamente
>     public void pagar(String tipo, double monto) {
>         estrategias.get(tipo).pagar(monto);
>     }
> }
> ```"

### Observer
Notificar a multiples objetos cuando cambia un estado. Patron de eventos.

```java
interface Observador { void actualizar(String mensaje); }

class Usuario implements Observador {
    private String nombre;
    public Usuario(String nombre) { this.nombre = nombre; }
    public void actualizar(String mensaje) {
        System.out.println(nombre + " recibio: " + mensaje);
    }
}

class Canal {
    private List<Observador> suscriptores = new ArrayList<>();
    public void suscribir(Observador obs) { suscriptores.add(obs); }
    public void notificar(String mensaje) {
        suscriptores.forEach(o -> o.actualizar(mensaje));
    }
}
```

> **Tip entrevista:** "En Spring Boot uso `@EventListener` y `ApplicationEventPublisher`. En microservicios, Kafka o RabbitMQ implementan Observer a nivel distribuido."

---

## 4. DISTRIBUIDOS

### Saga Pattern (microservicios)

Patron para manejar **transacciones distribuidas** entre multiples microservicios. Como no puedes hacer un `@Transactional` que abarque varios servicios, Saga coordina una secuencia de transacciones locales con compensaciones si algo falla.

```
Ejemplo: Crear un pedido involucra 3 servicios

Exito (happy path):
  Pedido-Service: crear pedido → Inventario-Service: reservar stock → Pago-Service: cobrar
                                                                        ✓ Todo OK

Fallo con compensacion:
  Pedido-Service: crear pedido → Inventario-Service: reservar stock → Pago-Service: cobrar ✗ FALLA
                                 Inventario-Service: LIBERAR stock ← compensacion
  Pedido-Service: CANCELAR pedido ← compensacion
```

**Dos enfoques:**

```java
// 1. COREOGRAFIA (event-driven, sin coordinador central)
// Cada servicio escucha eventos y reacciona
// Ventaja: desacoplado, simple para pocas sagas
// Desventaja: dificil de seguir el flujo con muchos servicios

@Service
public class PedidoService {
    public void crearPedido(PedidoRequest request) {
        Pedido pedido = repository.save(new Pedido(request, "PENDIENTE"));
        kafkaTemplate.send("pedido.creado", new PedidoCreadoEvent(pedido));
    }

    @KafkaListener(topics = "pago.fallido")
    public void compensar(PagoFallidoEvent evento) {
        Pedido pedido = repository.findById(evento.getPedidoId());
        pedido.setEstado("CANCELADO");
        repository.save(pedido);
    }
}

@Service
public class InventarioService {
    @KafkaListener(topics = "pedido.creado")
    public void reservarStock(PedidoCreadoEvent evento) {
        inventario.reservar(evento.getProductoId(), evento.getCantidad());
        kafkaTemplate.send("stock.reservado", new StockReservadoEvent(evento));
    }

    @KafkaListener(topics = "pago.fallido")
    public void compensar(PagoFallidoEvent evento) {
        inventario.liberar(evento.getProductoId(), evento.getCantidad());
    }
}

// 2. ORQUESTACION (coordinador central)
// Un orquestador controla el flujo y las compensaciones
// Ventaja: flujo claro, facil de debuggear
// Desventaja: punto unico de fallo (el orquestador)

@Service
public class PedidoSagaOrchestrator {

    public void ejecutar(PedidoRequest request) {
        try {
            // Paso 1: crear pedido
            Pedido pedido = pedidoService.crear(request);

            // Paso 2: reservar inventario
            inventarioClient.reservar(pedido.getProductoId(), pedido.getCantidad());

            // Paso 3: procesar pago
            pagoClient.cobrar(pedido.getTotal(), pedido.getUsuarioId());

            pedidoService.confirmar(pedido.getId());

        } catch (PagoException e) {
            // Compensar: liberar inventario + cancelar pedido
            inventarioClient.liberar(pedido.getProductoId(), pedido.getCantidad());
            pedidoService.cancelar(pedido.getId());
        } catch (InventarioException e) {
            // Compensar: solo cancelar pedido
            pedidoService.cancelar(pedido.getId());
        }
    }
}
```

> **Tip entrevista:** "Saga es la alternativa a transacciones distribuidas (2PC) que no escalan en microservicios. Uso coreografia cuando hay pocos servicios y orquestacion cuando el flujo es complejo. Cada servicio define su transaccion local y su compensacion."

---

## RESPUESTAS RAPIDAS - PATRONES

| Pregunta | Respuesta |
|----------|-----------|
| Singleton para que? | Una sola instancia global (BD, config). En Spring, @Service ya es Singleton |
| Factory para que? | Crear objetos sin exponer logica de creacion, evita switch/if largo |
| Builder para que? | Construir objetos complejos paso a paso, evita constructores con 10 params |
| Adapter para que? | Hacer compatible una interfaz incompatible (ej: sistema legacy) |
| Decorator para que? | Agregar funcionalidad sin modificar la clase original, se apilan |
| Strategy para que? | Cambiar algoritmo en runtime sin if/else (ej: metodos de pago) |
| Observer para que? | Notificar a multiples objetos cuando cambia un estado (eventos) |
| Saga para que? | Transacciones distribuidas en microservicios con compensacion si falla |
| Saga coreografia vs orquestacion? | Coreografia: eventos, desacoplado. Orquestacion: coordinador central, flujo claro |

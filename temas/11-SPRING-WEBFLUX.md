# SPRING WEBFLUX (Programacion Reactiva)

---

## 1. Que es?

Framework **reactivo** de Spring para APIs **no bloqueantes** y asincronicas, basado en **Project Reactor**.

| | Spring MVC | Spring WebFlux |
|---|---|---|
| Modelo | Bloqueante (1 thread por request) | No bloqueante (event loop) |
| Retorno | Objetos directos | `Mono<T>` / `Flux<T>` |
| Servidor | Tomcat | Netty (por defecto) |
| Cuando usar | CRUD tradicional, baja concurrencia | Alta concurrencia, streaming, microservicios |

---

## 2. Mono y Flux

- **Mono<T>**: 0 o 1 elemento (como Optional reactivo)
- **Flux<T>**: 0 o N elementos (como Stream reactivo)

```java
// Mono: un solo resultado
Mono<String> mono = Mono.just("Hola");
Mono<String> vacio = Mono.empty();
Mono<String> error = Mono.error(new RuntimeException("Error"));

// Flux: multiples resultados
Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);
Flux<Integer> rango = Flux.range(1, 10);
Flux<String> desdeLista = Flux.fromIterable(List.of("A", "B", "C"));
```

---

## 3. Operadores principales

```java
// map: transformar
Mono<String> upper = Mono.just("hola").map(String::toUpperCase); // "HOLA"

// flatMap: transformar a otro Mono/Flux (para llamadas asincronas)
Mono<UsuarioDTO> usuario = Mono.just(1L)
    .flatMap(id -> repository.findById(id));

// filter
Flux<Integer> pares = Flux.range(1, 10).filter(n -> n % 2 == 0);

// zip: combinar resultados de multiples fuentes
Mono<Tuple2<Usuario, List<Pedido>>> resultado = Mono.zip(
    usuarioService.buscar(id),
    pedidoService.buscarPorUsuario(id).collectList()
);

// onErrorResume: fallback en caso de error
Mono<String> seguro = servicio.llamar()
    .onErrorResume(ex -> Mono.just("Valor por defecto"));

// switchIfEmpty: si no hay resultado
Mono<Usuario> usuario = repository.findById(id)
    .switchIfEmpty(Mono.error(new NotFoundException("No encontrado")));
```

---

## 4. Controller reactivo

```java
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    @GetMapping
    public Flux<ProductoDTO> listar() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductoDTO>> obtener(@PathVariable String id) {
        return service.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
        return service.crear(dto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> eliminar(@PathVariable String id) {
        return service.eliminar(id);
    }
}
```

---

## 5. WebClient (cliente HTTP reactivo)

```java
@Service
public class UsuarioClient {
    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://usuario-service").build();
    }

    public Mono<UsuarioDTO> obtener(Long id) {
        return webClient.get()
            .uri("/api/usuarios/{id}", id)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                resp -> Mono.error(new NotFoundException("No encontrado")))
            .bodyToMono(UsuarioDTO.class)
            .timeout(Duration.ofSeconds(5))
            .retry(3);
    }
}
```

---

## RESPUESTAS RAPIDAS - WEBFLUX

| Pregunta | Respuesta |
|----------|-----------|
| Que es WebFlux? | Framework reactivo de Spring, no bloqueante, basado en Project Reactor |
| Mono vs Flux? | Mono: 0 o 1 elemento. Flux: 0 o N elementos |
| Cuando usar WebFlux? | Alta concurrencia (miles de conexiones), streaming, microservicios |
| Cuando NO usar? | CRUD simple con baja carga. Spring MVC es mas simple y suficiente |
| map vs flatMap? | map transforma sincrono. flatMap transforma a otro Mono/Flux (async) |
| Que reemplaza a RestTemplate? | WebClient (reactivo, no bloqueante) |
| Servidor por defecto? | Netty (no Tomcat) |

# API REST, SOAP y Documentacion

---

## 1. Que es RESTful?

Estilo de arquitectura para APIs basado en **recursos** y **verbos HTTP**. Una API es RESTful si cumple:

- **Recursos identificados por URLs**: `/api/usuarios/1`
- **Verbos HTTP para acciones**: GET, POST, PUT, PATCH, DELETE
- **Sin estado (Stateless)**: cada request tiene toda la info necesaria
- **Respuestas con codigos HTTP estandar**

```
GET    /api/usuarios          → Listar todos
GET    /api/usuarios/1        → Obtener uno por ID
POST   /api/usuarios          → Crear nuevo
PUT    /api/usuarios/1        → Actualizar completo (reemplaza todo)
PATCH  /api/usuarios/1        → Actualizar parcial (solo campos enviados)
DELETE /api/usuarios/1        → Eliminar
```

---

## 2. PUT vs PATCH (muy preguntado)

| | PUT | PATCH |
|---|---|---|
| **Que hace** | Reemplaza TODO el recurso | Actualiza SOLO los campos enviados |
| **Body** | Objeto completo | Solo los campos a cambiar |
| **Idempotente** | Si | Si |
| **Si omito un campo** | Se pone en null/default | No se modifica |

```java
// PUT: envias TODO el objeto (si omites email, se pone null)
// PUT /api/usuarios/1
// Body: { "nombre": "Carlos", "email": "carlos@mail.com", "edad": 26 }

@PutMapping("/{id}")
public ResponseEntity<UsuarioDTO> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody UsuarioDTO dto) {
    return ResponseEntity.ok(service.actualizar(id, dto));
}

// PATCH: envias SOLO lo que cambias
// PATCH /api/usuarios/1
// Body: { "edad": 26 }

@PatchMapping("/{id}")
public ResponseEntity<UsuarioDTO> actualizarParcial(
        @PathVariable Long id,
        @RequestBody Map<String, Object> campos) {
    return ResponseEntity.ok(service.actualizarParcial(id, campos));
}
```

> **Tip entrevista:** "Uso PUT cuando el cliente envia el objeto completo. PATCH cuando solo envia los campos que cambiaron. En la practica, muchos equipos usan PUT para todo, pero PATCH es mas eficiente para actualizaciones parciales."

---

## 3. Codigos HTTP que debes conocer

| Codigo | Significado | Cuando usar |
|--------|-------------|-------------|
| 200 | OK | GET exitoso, PUT/PATCH exitoso |
| 201 | Created | POST exitoso (recurso creado) |
| 204 | No Content | DELETE exitoso (sin body) |
| 400 | Bad Request | Datos invalidos del cliente |
| 401 | Unauthorized | No autenticado (sin token) |
| 403 | Forbidden | Autenticado pero sin permisos |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Conflicto (ej: email duplicado) |
| 500 | Internal Server Error | Error del servidor |
| 503 | Service Unavailable | Servicio caido |

---

## 4. SOAP vs REST

| | REST | SOAP |
|---|---|---|
| **Formato** | JSON (ligero) | XML (pesado) |
| **Protocolo** | HTTP | HTTP, SMTP, TCP |
| **Contrato** | Opcional (OpenAPI) | Obligatorio (WSDL) |
| **Estilo** | Recursos + verbos HTTP | Operaciones/funciones |
| **Rendimiento** | Rapido, ligero | Mas lento, mas pesado |
| **Seguridad** | HTTPS + JWT/OAuth2 | WS-Security (mas robusto) |
| **Estado** | Stateless | Puede ser stateful |
| **Cuando usar** | APIs web, mobile, microservicios | Banca, gobierno, sistemas enterprise legacy |

```xml
<!-- SOAP: request en XML -->
<soap:Envelope>
  <soap:Body>
    <ObtenerUsuario>
      <id>1</id>
    </ObtenerUsuario>
  </soap:Body>
</soap:Envelope>
```

```json
// REST: request simple
// GET /api/usuarios/1
// Response:
{
  "id": 1,
  "nombre": "Carlos",
  "email": "carlos@mail.com"
}
```

> **Tip entrevista:** "REST es el estandar para APIs modernas por su simplicidad y rendimiento. SOAP se usa en sistemas legacy, banca y gobierno donde se necesita WS-Security y contratos estrictos (WSDL)."

---

## 5. OpenAPI y Swagger

**OpenAPI** es la especificacion para documentar APIs REST. **Swagger** es la herramienta que genera la UI interactiva.

```xml
<!-- Dependencia Spring Boot -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

```java
// Documentar el controller
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Operaciones de gestion de usuarios")
public class UsuarioController {

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}

// Acceder a la documentacion:
// http://localhost:8080/swagger-ui.html
```

**Code First vs Contract First:**

| Enfoque | Como funciona |
|---------|---------------|
| **Code First** | Escribes el codigo Java y Swagger genera la documentacion automaticamente |
| **Contract First** | Escribes el YAML/JSON de OpenAPI primero y generas el codigo a partir de el |

> **Tip entrevista:** "En mis proyectos uso Code First con springdoc-openapi. Las anotaciones `@Operation` y `@ApiResponse` documentan los endpoints y Swagger genera la UI automaticamente en `/swagger-ui.html`."

---

## 6. DTO - Buenas practicas

```java
// DTO de REQUEST (lo que el cliente envia)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CrearUsuarioRequest {
    @NotBlank private String nombre;
    @Email private String email;
    @Min(18) private Integer edad;
}

// DTO de RESPONSE (lo que retornas al cliente)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String estado;
    // NUNCA exponer: password, tokens, datos internos
}

// Mapper: Entity ↔ DTO (manual o con MapStruct)
@Component
public class UsuarioMapper {
    public UsuarioResponse toResponse(Usuario entity) {
        return UsuarioResponse.builder()
            .id(entity.getId())
            .nombre(entity.getNombre())
            .email(entity.getEmail())
            .estado(entity.getEstado().name())
            .build();
    }
}
```

> **Tip entrevista:** "Separo DTOs de request y response. Nunca expongo la entidad JPA directamente al cliente porque: 1) puede exponer datos sensibles, 2) acopla la API a la BD, 3) cambios en la BD rompen la API."

---

## 7. Token de acceso y OAuth2

**Token de acceso (JWT):** cadena codificada que contiene la identidad del usuario y sus permisos. Se envia en cada request en el header `Authorization: Bearer <token>`.

**OAuth2:** protocolo de autorizacion que permite a una app acceder a recursos de otra en nombre del usuario.

```
Flujo basico OAuth2:
1. Usuario inicia sesion → Auth Server
2. Auth Server valida credenciales → genera Access Token + Refresh Token
3. Cliente envia Access Token en cada request → API
4. API valida el token → retorna datos
5. Si Access Token expira → usa Refresh Token para obtener uno nuevo
```

```java
// Validar JWT en Spring Security
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain) {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Claims claims = jwtService.validar(token);
            // establecer autenticacion en el SecurityContext
        }
        chain.doFilter(request, response);
    }
}
```

---

## RESPUESTAS RAPIDAS - API

| Pregunta | Respuesta |
|----------|-----------|
| Que es RESTful? | Arquitectura basada en recursos + verbos HTTP, stateless, con codigos HTTP estandar |
| PUT vs PATCH? | PUT reemplaza todo el recurso. PATCH actualiza solo los campos enviados |
| REST vs SOAP? | REST: JSON, ligero, moderno. SOAP: XML, pesado, contratos WSDL, banca/gobierno |
| Que es OpenAPI/Swagger? | Especificacion para documentar APIs REST con UI interactiva |
| Code First? | Escribes codigo Java y la documentacion se genera automaticamente |
| Que es un token? | Cadena (JWT) con identidad y permisos, se envia en header Authorization |
| Que es OAuth2? | Protocolo de autorizacion para acceder a recursos en nombre del usuario |
| Por que usar DTOs? | No exponer la entidad directamente, seguridad, desacoplamiento API-BD |

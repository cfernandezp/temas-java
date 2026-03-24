# Seguridad en Integraciones

---

## 1. Seguridad en el sector financiero

En microfinanzas, la seguridad es critica por regulaciones (SBS, PCI-DSS) y la sensibilidad de los datos financieros.

```
[Canal]          [API Gateway]        [Middleware]         [Core Bantotal]
App/Web ──mTLS──→ OAuth2/JWT ──mTLS──→ Camel Routes ──TLS──→ BTServices
                  Rate Limit           Validacion          Credenciales
                  WAF                  Auditoria           Vault
```

---

## 2. Autenticacion y autorizacion

### OAuth2 + JWT

```java
// Validar JWT en ruta Camel
from("rest:post:/api/transferencias")
    .routeId("transferenciasSecure")
    .process(exchange -> {
        String authHeader = exchange.getIn().getHeader("Authorization", String.class);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Token no proporcionado");
        }
        String token = authHeader.substring(7);

        // Validar JWT
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        // Verificar permisos
        List<String> roles = claims.get("roles", List.class);
        if (!roles.contains("TRANSFERENCIAS_WRITE")) {
            throw new SecurityException("Permiso insuficiente");
        }

        // Propagar datos del usuario
        exchange.getIn().setHeader("usuario", claims.getSubject());
        exchange.getIn().setHeader("canal", claims.get("canal"));
    })
    .to("direct:ejecutarTransferencia");
```

### Seguridad en JBoss EAP

```xml
<!-- Configurar security domain en JBoss para la aplicacion middleware -->
<security-domain name="middleware-security" cache-type="default">
    <authentication>
        <login-module code="Database" flag="required">
            <module-option name="dsJndiName" value="java:jboss/datasources/SecurityDS"/>
            <module-option name="principalsQuery"
                           value="SELECT password FROM users WHERE username=?"/>
            <module-option name="rolesQuery"
                           value="SELECT role, 'Roles' FROM user_roles WHERE username=?"/>
        </login-module>
    </authentication>
</security-domain>
```

---

## 3. mTLS (Mutual TLS)

Autenticacion bidireccional: tanto cliente como servidor presentan certificado.

```java
// Configurar mTLS en Camel para conectar con Bantotal
from("direct:llamarBantotal")
    .to("https4://bantotal:8443/BTServices/api/operacion"
        + "?sslContextParameters=#sslContext"
        + "&x509HostnameVerifier=#hostnameVerifier");

// Configuracion del SSL Context
@Bean
public SSLContextParameters sslContext() {
    KeyStoreParameters keyStore = new KeyStoreParameters();
    keyStore.setResource("classpath:certs/middleware-keystore.jks");
    keyStore.setPassword("{{keystore.password}}");

    KeyStoreParameters trustStore = new KeyStoreParameters();
    trustStore.setResource("classpath:certs/truststore.jks");
    trustStore.setPassword("{{truststore.password}}");

    KeyManagersParameters keyManagers = new KeyManagersParameters();
    keyManagers.setKeyStore(keyStore);
    keyManagers.setKeyPassword("{{key.password}}");

    TrustManagersParameters trustManagers = new TrustManagersParameters();
    trustManagers.setKeyStore(trustStore);

    SSLContextParameters sslContextParams = new SSLContextParameters();
    sslContextParams.setKeyManagers(keyManagers);
    sslContextParams.setTrustManagers(trustManagers);
    sslContextParams.setSecureSocketProtocol("TLSv1.3");

    return sslContextParams;
}
```

```yaml
# application.yml
server:
  ssl:
    enabled: true
    key-store: classpath:certs/middleware-keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    client-auth: need              # Requiere certificado del cliente
    trust-store: classpath:certs/truststore.p12
    trust-store-password: ${TRUSTSTORE_PASSWORD}
    protocol: TLSv1.3
```

---

## 4. Gestion de credenciales

### Vault / Credential Store

```java
// Nunca hardcodear credenciales!

// Opcion 1: Variables de entorno (OpenShift Secrets)
@Value("${BANTOTAL_PASSWORD}")
private String btPassword;

// Opcion 2: JBoss Credential Store (EAP 7.4+)
// En standalone.xml
// <credential-store name="my-store" location="/opt/jboss/credentials/store.jceks"/>
// Referencia: ${CREDENTIAL_STORE::my-store::bt-password}

// Opcion 3: HashiCorp Vault
@Configuration
public class VaultConfig {
    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setHost("vault.banco.internal");
        endpoint.setPort(8200);
        return new VaultTemplate(endpoint, new TokenAuthentication("s.xxx"));
    }
}

// Usar en Camel
from("direct:operacion")
    .process(exchange -> {
        VaultResponse response = vaultTemplate.read("secret/data/bantotal");
        String password = (String) response.getData().get("password");
        exchange.getIn().setHeader("bt-password", password);
    });
```

---

## 5. Cifrado de datos sensibles

```java
// Cifrar datos sensibles en transito entre servicios
@Component
public class CifradoProcessor implements Processor {

    @Value("${encryption.key}")
    private String encryptionKey;

    @Override
    public void process(Exchange exchange) throws Exception {
        String datos = exchange.getIn().getBody(String.class);

        // AES-256 para cifrar datos sensibles
        SecretKeySpec keySpec = new SecretKeySpec(
            encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(128, iv));

        byte[] encrypted = cipher.doFinal(datos.getBytes(StandardCharsets.UTF_8));
        String resultado = Base64.getEncoder().encodeToString(iv) + "."
                         + Base64.getEncoder().encodeToString(encrypted);

        exchange.getIn().setBody(resultado);
    }
}

// Enmascarar datos en logs
from("jms:queue:transacciones")
    .log("Procesando transaccion cliente: ${body.clienteId}, monto: ***")  // No loggear monto
    .bean(transaccionService)
    .log("Transaccion completada: ${header.transaccionId}");
```

---

## 6. Auditoria y trazabilidad

```java
// Audit trail para toda operacion financiera
@Component
public class AuditoriaRoute extends RouteBuilder {
    @Override
    public void configure() {
        // Interceptar TODAS las rutas para auditoria
        interceptFrom("rest:*")
            .process(exchange -> {
                AuditEvent event = new AuditEvent();
                event.setTimestamp(Instant.now());
                event.setUsuario(exchange.getIn().getHeader("usuario", String.class));
                event.setCanal(exchange.getIn().getHeader("canal", String.class));
                event.setOperacion(exchange.getIn().getHeader(Exchange.HTTP_PATH, String.class));
                event.setMetodo(exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class));
                event.setIpOrigen(exchange.getIn().getHeader("X-Forwarded-For", String.class));
                event.setCorrelationId(exchange.getExchangeId());

                exchange.setProperty("auditEvent", event);
            });

        // Guardar auditoria al final de cada operacion
        interceptSendToEndpoint("jms:queue:*-completado")
            .afterUri("jms:queue:*-completado")
            .process(exchange -> {
                AuditEvent event = exchange.getProperty("auditEvent", AuditEvent.class);
                event.setResultado("OK");
                event.setDuracionMs(System.currentTimeMillis() - event.getTimestamp().toEpochMilli());
            })
            .wireTap("kafka:auditoria-transacciones");
    }
}
```

---

## 7. PCI-DSS en integraciones

| Requisito PCI-DSS | Implementacion en Middleware |
|--------------------|------------------------------|
| Cifrar datos en transito | TLS 1.2+ en todas las conexiones |
| No almacenar datos de tarjeta | Tokenizacion, nunca persistir PAN completo |
| Control de acceso | RBAC, OAuth2, principio de minimo privilegio |
| Logging de acceso | Audit trail de toda operacion con datos de tarjeta |
| Segmentar red | Namespaces separados en OpenShift, Network Policies |
| Pruebas de seguridad | Escaneo de vulnerabilidades, pentesting periodico |
| Cifrar datos en reposo | Cifrado de BD, secrets de OpenShift, Vault |

---

## Preguntas de entrevista

### Basicas
1. **Como securizas la comunicacion entre el middleware y Bantotal?**
   - TLS/mTLS para cifrado en transito, autenticacion con token, credenciales en Vault
2. **Que es mTLS?**
   - Mutual TLS: autenticacion bidireccional donde ambos extremos presentan certificado
3. **Donde almacenas credenciales de integracion?**
   - Nunca en codigo. Usar OpenShift Secrets, Credential Store de JBoss, o HashiCorp Vault

### Intermedias
4. **Como implementas auditoria de operaciones financieras?**
   - Interceptor en Camel que captura metadata de cada operacion, wireTap a Kafka/BD de auditoria
5. **Que es PCI-DSS y como afecta al middleware?**
   - Estandar de seguridad para datos de tarjetas. Afecta cifrado, logging, acceso, tokenizacion
6. **Como manejas la rotacion de certificados?**
   - Cert-manager en OpenShift, monitoear vencimiento, rolling restart de pods

### Avanzadas
7. **Como implementas el principio de minimo privilegio en integraciones?**
   - Usuarios de servicio dedicados por ruta, permisos granulares en Bantotal por canal, RBAC en OpenShift
8. **Como previenes ataques de inyeccion en servicios SOAP/REST?**
   - Validacion de schema XML/JSON, sanitizacion de inputs, WAF en API Gateway

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| mTLS? | TLS bidireccional: ambos presentan certificado |
| Donde guardar credenciales? | Vault, OpenShift Secrets, Credential Store |
| JWT en Camel? | Validar en Processor, extraer claims, verificar roles |
| Auditoria? | Interceptor + wireTap a Kafka/BD |
| PCI-DSS? | Cifrado, no almacenar PAN, logging, segmentacion |
| WS-Security? | Seguridad a nivel de mensaje SOAP |
| Cifrado datos? | AES-256-GCM para datos sensibles |
| Enmascarar logs? | Nunca loguear datos financieros completos |

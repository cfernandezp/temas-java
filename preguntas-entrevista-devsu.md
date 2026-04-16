# Entrevista Devsu - Senior Desarrollador Java (Spring) - Sector Financiero

## Fundamentos y Buenas Prácticas

### 1. ¿Cómo funciona git bisect y en qué situaciones lo usarías?
Git bisect es una herramienta que te ayuda a encontrar el commit exacto que introdujo un bug usando búsqueda binaria. Tú le indicas un commit donde todo funcionaba bien y otro donde ya está el error, y git bisect va partiendo el historial a la mitad. Tú le marcas si ese punto es bueno o malo, y así va reduciendo hasta dar con el commit exacto que causó el problema. Lo he usado cuando aparece un bug en producción y no es claro qué cambio lo provocó, sobre todo cuando hay muchos commits entre la última versión estable y la actual.

### 2. Explique cómo optimizar una consulta en una base de datos relacional y el uso de EXPLAIN para entender su plan de ejecución.
Lo primero que hago es usar EXPLAIN o EXPLAIN ANALYZE para ver el plan de ejecución. Esto me muestra cómo el motor de base de datos va a resolver la consulta, si está haciendo un sequential scan, si está usando índices o no, cuántas filas estima procesar y el costo estimado. Con esa información tomo decisiones: si veo un full table scan en tabla grande, probablemente falte un índice. También evito SELECT *, uso índices compuestos cuando filtro por varias columnas, y evito funciones en el WHERE porque invalidan índices.

### 3. ¿Puedes explicar los métodos HTTP y los códigos de estado en el contexto del desarrollo de API?
Los métodos principales: GET para consultar, POST para crear, PUT para actualizar completamente, PATCH para actualizaciones parciales y DELETE para eliminar. Códigos de estado: 200 exitoso, 201 recurso creado, 204 sin contenido, 400 bad request, 401 no autenticado, 403 sin permisos, 404 no encontrado, 409 conflicto, 500 error del servidor, 503 servicio no disponible. Estandarizo las respuestas de error con un exception handler global usando @RestControllerAdvice.

### 4. Explica cómo aplicas los principios SOLID en tus desarrollos en Java.
- **S (Responsabilidad Única):** Cada clase tiene un solo propósito. Separo bien controlador, servicio y repositorio.
- **O (Abierto/Cerrado):** Uso interfaces e implementaciones. Si necesito agregar un nuevo comportamiento, creo una nueva implementación sin tocar las existentes.
- **L (Liskov):** Las clases hijas pueden reemplazar a las padres sin romper nada.
- **I (Segregación de Interfaces):** Interfaces pequeñas y específicas en vez de una gigante.
- **D (Inversión de Dependencias):** Inyecto dependencias por constructor usando interfaces, facilita el testing y desacopla el código.

### 5. ¿Qué es un DTO y cuál es su propósito? ¿Cómo difiere una entidad de dominio?
Un DTO es un objeto para transportar datos entre capas, controlar qué información expongo y recibo. La entidad de dominio representa una tabla en la base de datos con anotaciones JPA. La diferencia: la entidad está ligada a la base de datos y el DTO a la comunicación con el exterior. Uso DTO de request para recibir datos con validaciones, y DTO de response para devolver solo lo necesario. Para el mapeo uso MapStruct.

### 6. Menciona alguna buena práctica que sigas para mantener una sintaxis limpia y comprensible en tus desarrollos en Java.
Nombres descriptivos en variables, métodos y clases. Métodos cortos con una sola responsabilidad. Evito valores hardcodeados, uso constantes. Sigo convenciones estándar de Java. Organizo paquetes por dominio. Uso Lombok para reducir boilerplate. Y Code Review en equipo para verificar que el código se entienda para otros.

---

## Desarrollo con Java y Spring Boot

### 7. ¿Cómo manejarías excepciones de forma global en una aplicación Spring Boot?
Uso @RestControllerAdvice con métodos @ExceptionHandler para cada tipo de excepción: validación, recurso no encontrado, errores de negocio y uno genérico. Cada uno devuelve una respuesta estandarizada con código de estado, mensaje y detalles. Centralizo el manejo de errores en un solo lugar, no hay try-catch regados por los controladores.

### 8. ¿Cómo manejarías transacciones en Spring Boot y cuándo usarías REQUIRED versus REQUIRES_NEW?
REQUIRED (por defecto): si ya existe una transacción el método se une, si no existe crea una nueva. Lo uso en la mayoría de casos. REQUIRES_NEW: ejecuta en su propia transacción independiente. Lo uso por ejemplo para registro de auditoría en operaciones financieras, donde si falla la operación principal, el log de auditoría sí debe quedar guardado. Importante: @Transactional funciona por proxies, la llamada debe venir desde otra clase.

### 9. Explica cómo manejar validaciones en Spring Boot usando anotaciones y validaciones personalizadas.
Uso Bean Validation (@NotNull, @NotBlank, @Size, @Email) en los DTOs, y @Valid en el controlador. Para validaciones específicas creo anotaciones personalizadas con ConstraintValidator. Errores de validación los capturo en @RestControllerAdvice. Validaciones de negocio complejas las manejo en la capa de servicio con excepciones personalizadas.

### 10. Explica qué es un Patrón Saga y en qué tipo de arquitectura lo implementarías.
Patrón para transacciones distribuidas en microservicios. Divide la operación en transacciones locales, si alguna falla se ejecutan transacciones compensatorias. Dos formas: coreografía (eventos) y orquestación (coordinador central). Prefiero orquestación cuando el flujo es complejo. Aplica en microservicios, en monolito no se necesita.

### 11. ¿Qué patrones de diseño ha aplicado en tus proyectos recientes?
- **Strategy:** Diferentes métodos de pago con una interfaz y una implementación por cada medio. Permite agregar nuevos sin modificar los existentes.
- **Builder:** Para objetos con muchos atributos, código más legible. Con Lombok usando @Builder.

### 12. ¿En qué situaciones optarías por una arquitectura tradicional y en qué aplicarías una arquitectura hexagonal?
Tradicional: proyectos sencillos, CRUDs, poca lógica de negocio, equipos pequeños. Hexagonal: lógica de negocio compleja que necesita estar aislada de la infraestructura. Ventaja: testeabilidad (probar lógica sin dependencias externas) y trabajo en paralelo del equipo.

### 13. ¿Cuándo usarías una arquitectura de microservicios en lugar de una monolítica?
Microservicios: cuando necesitas escalar de forma independiente, equipos grandes, módulos con necesidades tecnológicas distintas. Monolito: proyecto empezando, equipo pequeño, lógica no definida. Lo sano es empezar monolítico y migrar gradualmente cuando crece la demanda.

### 14. ¿Cuál es tu experiencia con programación reactiva y funcional?
Programación reactiva con Spring WebFlux y Project Reactor para alta concurrencia. Mono y Flux para manejar miles de conexiones sin bloquear hilos. Programación funcional con Streams, lambdas, Optional, CompletableFuture para transformar colecciones y hacer código más legible. No siempre es mejor usar reactivo, depende de la necesidad real.

---

## Seguridad en Desarrollo

### 15. ¿Qué prácticas de seguridad aplicas para evitar vulnerabilidades como inyección SQL o XSS?
Para SQL injection: queries parametrizadas con JPA, nunca concatenar valores. Para XSS: validar y sanitizar entradas, headers de seguridad como Content-Security-Policy. Además: principio de mínimo privilegio, dependencias actualizadas, OWASP Dependency Check en CI/CD.

### 16. ¿Qué herramientas y procesos utilizaría para detectar vulnerabilidades antes del despliegue?
SonarQube para análisis estático, OWASP Dependency Check para dependencias, Trivy para escanear imágenes Docker. Code Review enfocado en seguridad. Pruebas de integración que verifiquen controles de seguridad. Todo automatizado en el pipeline de CI/CD.

### 17. ¿Cómo implementarías seguridad en una API REST con Spring Boot usando JWT u OAuth2?
Spring Security + JWT: usuario se autentica, servidor genera token JWT, cliente lo envía en header Authorization. Filtro intercepta cada request, valida token, setea autenticación en SecurityContext. @PreAuthorize para control por roles. OAuth2 para integrar proveedores externos. Tokens con expiración corta y refresh tokens para renovar acceso.

---

## Pruebas y Calidad de Software

### 18. ¿Cómo implementarías pruebas de contrato y pruebas de mutación en microservicios?
Pruebas de contrato con Spring Cloud Contract: productor define contrato, se generan tests y stubs automáticamente. Pruebas de mutación con PITest: modifica código y verifica que los tests detecten los cambios. Ambas integradas en CI/CD.

### 19. ¿Cómo manejarías una prueba de integración entre dos microservicios?
@SpringBootTest para levantar contexto, WireMock para simular el otro servicio, Testcontainers para base de datos real. Prueba completa de controller a persistencia. Más cercano a producción que usar mocks o H2.

### 20. ¿Qué patrón de diseño utilizarías para mejorar la mantenibilidad de tus pruebas unitarias?
Builder para construir objetos de prueba. Object Mother para objetos preconfigurados por escenario. Estructura Arrange, Act, Assert. Tests independientes con nombres descriptivos.

### 21. ¿Cómo implementas pruebas usando Karate?
Framework para pruebas de APIs con sintaxis tipo Gherkin sin step definitions en Java. Archivos .feature con Given, When, Then. Fácil de leer, permite encadenar llamadas. Para contract testing en microservicios prefiero Spring Cloud Contract por su integración con Spring.

---

## DevOps y Despliegue

### 22. ¿Cómo solucionaría un error en una tubería de CI/CD?
Revisar logs del pipeline para ver en qué etapa falló. Según la etapa, revisar dependencias, tests, variables de entorno o configuración. Reproducir en local. Hacer fix, push y verificar que el pipeline pase completo. Ajustar configuración para prevenir a futuro.

### 23. Si un servicio en Kubernetes tiene fallos intermitentes, ¿qué pasos seguirías?
kubectl get pods para ver reinicios o CrashLoopBackOff. kubectl logs para errores. kubectl describe pod para revisar límites de memoria/CPU (OOMKilled). Revisar liveness y readiness probes. Verificar conectividad entre servicios y conexiones a base de datos. Monitoreo con Prometheus y Grafana.

### 24. ¿Cómo usas Docker en tu día a día?
docker-compose para entorno de desarrollo completo. Testcontainers para pruebas de integración. Dockerfiles con multi-stage builds para imágenes livianas en producción. El mismo artefacto que pruebo en local es el que llega a producción.

### 25. ¿Qué estrategia de ramificación has utilizado?
Git Flow: main (producción), develop (integración), feature (funcionalidades), release (ajustes pre-producción), hotfix (bugs críticos). Pull requests obligatorios con al menos un aprobador.

### 26. ¿Cómo integrarías una aplicación Spring Boot con servicios en la nube?
Azure SDK para servicios como Blob Storage, Key Vault para secretos, Service Bus para mensajería. Docker + AKS para despliegue. ConfigMaps y Secrets de Kubernetes para configuraciones por ambiente. Application Insights, Prometheus y Grafana para monitoreo.

---

## English Skills

### 27. What are your short-term and long-term goals?
Short term: keep growing as senior backend developer, improve skills in cloud architecture and distributed systems. Long term: move into technical leadership/software architect role. Currently combining technical work with team guidance and architectural decisions. MBA in Business Intelligence and Big Data for broader business perspective.

### 28. Could you provide an example of what you are currently doing to improve as a professional?
Deepening knowledge in cloud-native architectures with Kubernetes and Azure. Staying up to date with Spring Boot features. Code reviews as a way to learn. Reading technical articles about microservices and system design. Combining learning with practice.

### 29. Could you tell us about your personal strategies for organizing your time?
Prioritize tasks based on impact and urgency. Use Jira to track everything and break down big tasks. Block time for focused work. Group meetings to avoid interrupting flow. Clear communication with the team through quick standups.

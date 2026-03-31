package com.middleware.creditos.route;

import com.middleware.creditos.model.ResultadoEvaluacion;
import com.middleware.creditos.model.SolicitudCredito;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  REST API ROUTE - Punto de entrada HTTP del middleware            ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Define los endpoints REST que expone el middleware usando el
 * REST DSL de Apache Camel.
 *
 * REST DSL vs Spring MVC (@RestController):
 * - Spring MVC: los endpoints se definen con @GetMapping, @PostMapping
 * - Camel REST DSL: los endpoints se definen en el RouteBuilder
 *   y se conectan directamente a rutas Camel con .to("direct:xxx")
 *
 * Ventaja del REST DSL: el request entra directamente al ecosistema
 * Camel (Exchange, headers, EIP) sin necesidad de un Controller
 * intermediario. Es mas natural en un proyecto de integracion.
 *
 * Los endpoints REST son la "puerta de entrada" desde los canales
 * digitales (app movil, web banking) hacia las rutas de integracion.
 *
 * Flujo:
 * App Movil → HTTP POST /api/creditos/solicitar → [RestApiRoute]
 *   → direct:nuevaSolicitud → [ContentBasedRouterRoute] → ... → Bantotal
 */
@Component
public class RestApiRoute extends RouteBuilder {

    /**
     * configure() es el metodo donde defines TODAS las rutas de este RouteBuilder.
     * Camel lo invoca automaticamente al arrancar el CamelContext.
     * Puedes tener multiples RouteBuilder en tu proyecto, cada uno
     * con sus propias rutas. Camel los registra todos.
     */
    @Override
    public void configure() throws Exception {

        // ============================================================
        // CONFIGURACION GLOBAL DEL REST DSL
        // Define como se comportan TODOS los endpoints REST.
        // Se configura UNA sola vez en todo el proyecto.
        // ============================================================
        restConfiguration()
                // Componente HTTP que recibe los requests.
                // "servlet" usa el Servlet de Spring Boot (Tomcat).
                .component("servlet")

                // Binding mode: como se serializa/deserializa el body.
                // JSON = automaticamente convierte body a/desde JSON con Jackson.
                // Equivalente a tener @RequestBody/@ResponseBody en Spring MVC.
                .bindingMode(RestBindingMode.json)

                // Context path: prefijo de todas las rutas REST.
                // URL final: http://localhost:8080/camel/api/...
                .contextPath("/api")

                // Formato bonito para desarrollo (quitar en produccion)
                .dataFormatProperty("prettyPrint", "true")

                // Metadata de la API para documentacion
                .apiProperty("api.title", "Middleware Creditos API")
                .apiProperty("api.version", "1.0.0");

        // ============================================================
        // ENDPOINTS DE CREDITOS
        // ============================================================
        rest("/creditos")
                // Descripcion para documentacion OpenAPI
                .description("Operaciones de credito del middleware")

                // ---- POST /api/creditos/solicitar ----
                // Recibe una nueva solicitud de credito y la procesa
                .post("/solicitar")
                .description("Crear nueva solicitud de credito")
                .consumes("application/json")       // Acepta JSON
                .produces("application/json")       // Devuelve JSON
                .type(SolicitudCredito.class)       // Tipo del body de entrada (para binding y doc)
                .outType(ResultadoEvaluacion.class) // Tipo del body de salida
                .to("direct:nuevaSolicitud")        // Conecta a la ruta de procesamiento
                // NOTA: "direct:nuevaSolicitud" es un endpoint sincrono en memoria.
                // El request HTTP espera hasta que la ruta complete y devuelva respuesta.

                // ---- POST /api/creditos/simular ----
                // Simula un credito sin crearlo (consulta a BTPrestamos.Simular)
                .post("/simular")
                .description("Simular credito en Bantotal sin crearlo")
                .consumes("application/json")
                .produces("application/json")
                .type(SolicitudCredito.class)
                .to("direct:simularBantotal");

        // ============================================================
        // ENDPOINTS DE OPERACIONES
        // ============================================================
        rest("/operaciones")
                .description("Operaciones generales del middleware")

                // ---- POST /api/operaciones/pago ----
                // Procesa un pago entrante (demuestra idempotencia)
                .post("/pago")
                .description("Procesar un pago (con control de idempotencia)")
                .consumes("application/json")
                .to("direct:procesarPago");

        // ============================================================
        // ENDPOINT DE HEALTH CHECK
        // Util para readinessProbe y livenessProbe en OpenShift/K8s
        // ============================================================
        rest("/health")
                .get("/ready")
                .description("Readiness probe para Kubernetes")
                .to("direct:healthReady")

                .get("/live")
                .description("Liveness probe para Kubernetes")
                .to("direct:healthLive");

        // ---- Rutas de health check ----
        from("direct:healthReady")
                .routeId("healthReady")
                .setBody(constant("{\"status\": \"READY\", \"service\": \"middleware-creditos\"}"));

        from("direct:healthLive")
                .routeId("healthLive")
                .setBody(constant("{\"status\": \"UP\", \"service\": \"middleware-creditos\"}"));
    }
}

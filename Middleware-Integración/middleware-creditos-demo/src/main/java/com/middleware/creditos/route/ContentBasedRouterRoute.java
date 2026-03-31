package com.middleware.creditos.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  CONTENT-BASED ROUTER - Patron EIP mas usado en middleware       ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PATRON: Content-Based Router (Enrutador basado en contenido)
 *
 * Rutea un mensaje a diferentes destinos segun el CONTENIDO del
 * mensaje (body o headers). Es el equivalente a un switch-case
 * pero para flujos de integracion.
 *
 * En Camel se implementa con: .choice().when().to().otherwise().end()
 *
 * CASO DE USO FINANCIERO:
 * Las solicitudes de credito se procesan diferente segun el tipo
 * de producto y el monto solicitado:
 * - PERSONAL < 10,000: aprobacion automatica (si score es bueno)
 * - PERSONAL >= 10,000: requiere evaluacion intermedia
 * - MICROEMPRESA: evaluacion especializada de microfinanzas
 * - HIPOTECARIO: aprobacion de gerencia (montos altos)
 *
 * Flujo:
 * direct:nuevaSolicitud
 *   → Validar
 *   → Enriquecer con score
 *   → CHOICE (Content-Based Router)
 *     ├── MICROEMPRESA → evaluacion microfinanzas
 *     ├── HIPOTECARIO  → evaluacion gerencia
 *     ├── PERSONAL (monto alto) → evaluacion intermedia
 *     ├── PERSONAL (monto bajo + buen score) → aprobacion automatica
 *     └── OTHERWISE → producto desconocido
 */
@Component
public class ContentBasedRouterRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ============================================================
        // MANEJO DE ERRORES para esta ruta
        // onException es mas granular que errorHandler global.
        // Puedes definir comportamiento diferente para cada tipo
        // de excepcion.
        // ============================================================

        // Errores de validacion: NO reintentar, devolver error 400
        onException(IllegalArgumentException.class)
                .handled(true)  // "handled" significa que Camel NO propaga la excepcion
                // El caller recibe una respuesta normal (no un error HTTP 500)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("{\"error\": \"${exception.message}\", \"tipo\": \"VALIDACION\"}"))
                .log(LoggingLevel.WARN, "Validacion fallida: ${exception.message}");

        // ============================================================
        // RUTA PRINCIPAL: Recibe solicitud y la enruta segun tipo
        // ============================================================
        from("direct:nuevaSolicitud")
                // routeId: identificador unico de esta ruta.
                // Aparece en logs, metricas, Fuse Console (Hawtio).
                // SIEMPRE ponle routeId descriptivo a cada ruta.
                .routeId("enrutarSolicitudCredito")

                // Log de entrada (${body} invoca toString() del body)
                .log(">>> Nueva solicitud recibida: ${body}")

                // PASO 1: Validar campos obligatorios
                // .process() invoca un Processor por nombre de bean Spring.
                // Equivalente a: .process(new ValidarSolicitudProcessor())
                .process("validarSolicitudProcessor")

                // PASO 2: Enriquecer con score crediticio
                // Consulta la central de riesgo y agrega el score al body
                .process("enriquecerScoreProcessor")

                .log("Score asignado: ${header.scoreCrediticio}, Riesgo: ${header.nivelRiesgo}")

                // PASO 3: Wire Tap para auditoria (copia sin bloquear)
                // Ver WireTapRoute.java para detalle del patron
                .wireTap("seda:auditoriaOperaciones")

                // ============================================================
                // PASO 4: CONTENT-BASED ROUTER
                //
                // .choice() inicia el router
                // .when(predicado) define cada condicion
                // .to(endpoint) envia al destino si la condicion se cumple
                // .otherwise() define el destino por defecto
                // .end() cierra el choice
                //
                // SIMPLE LANGUAGE: ${body.campo} accede a campos del body
                // via getters. ${body.tipoProducto} llama a getTipoProducto()
                // ============================================================
                .choice()

                // Ruta 1: MICROEMPRESA → Evaluacion especializada
                // Los creditos para microempresa requieren analisis de
                // capacidad de pago del negocio, no solo score personal
                .when(simple("${body.tipoProducto} == 'MICROEMPRESA'"))
                .log("Ruta: MICROEMPRESA → evaluacion especializada")
                .to("direct:evaluacionMicrofinanzas")

                // Ruta 2: HIPOTECARIO → Aprobacion de gerencia
                // Montos altos requieren aprobacion de nivel superior
                .when(simple("${body.tipoProducto} == 'HIPOTECARIO'"))
                .log("Ruta: HIPOTECARIO → aprobacion de gerencia")
                .to("direct:evaluacionGerencia")

                // Ruta 3: PERSONAL con monto alto (>= 10,000) → Evaluacion intermedia
                .when(simple("${body.tipoProducto} == 'PERSONAL' && ${body.monto} >= 10000"))
                .log("Ruta: PERSONAL monto alto → evaluacion intermedia")
                .to("direct:evaluacionIntermedia")

                // Ruta 4: PERSONAL con buen score y monto bajo → Automatica
                .when(simple("${body.tipoProducto} == 'PERSONAL' && ${body.scoreCrediticio} >= 650"))
                .log("Ruta: PERSONAL buen score → aprobacion automatica")
                .to("direct:aprobacionAutomatica")

                // Otherwise: cualquier otro caso
                .otherwise()
                .log(LoggingLevel.WARN, "Ruta: Producto no reconocido o score bajo → revision manual")
                .to("direct:revisionManual")

                .end(); // FIN del choice

        // ============================================================
        // SUB-RUTAS: Cada destino del Content-Based Router
        // ============================================================

        // --- Aprobacion automatica (score alto, monto bajo) ---
        from("direct:aprobacionAutomatica")
                .routeId("aprobacionAutomatica")
                .log("Procesando aprobacion automatica para solicitud ${body.id}")
                .process(exchange -> {
                    // Inline processor: puedes escribir la logica directamente
                    // sin crear una clase Processor separada.
                    // Util para logica simple. Para logica compleja, usa una clase.
                    var solicitud = exchange.getIn().getBody(
                            com.middleware.creditos.model.SolicitudCredito.class);
                    solicitud.setEstado("APROBADO");

                    var resultado = new com.middleware.creditos.model.ResultadoEvaluacion();
                    resultado.setSolicitudId(solicitud.getId());
                    resultado.setAprobado(true);
                    resultado.setMontoAprobado(solicitud.getMonto());
                    resultado.setTasaAsignada(determinarTasa(solicitud.getScoreCrediticio()));
                    resultado.setNivelRiesgo(solicitud.getNivelRiesgo());

                    exchange.getIn().setBody(resultado);
                })
                .log("<<< Solicitud APROBADA automaticamente: ${body}");

        // --- Evaluacion microfinanzas ---
        from("direct:evaluacionMicrofinanzas")
                .routeId("evaluacionMicrofinanzas")
                .log("Evaluacion MICROFINANZAS para solicitud ${body.id}")
                .process(exchange -> {
                    var solicitud = exchange.getIn().getBody(
                            com.middleware.creditos.model.SolicitudCredito.class);

                    var resultado = new com.middleware.creditos.model.ResultadoEvaluacion();
                    resultado.setSolicitudId(solicitud.getId());

                    // En microfinanzas, creditos < 20,000 con score >= 500
                    // se aprueban con tasa diferenciada
                    boolean aprobado = solicitud.getScoreCrediticio() >= 500
                            && solicitud.getMonto().intValue() <= 20000;

                    resultado.setAprobado(aprobado);
                    resultado.setMontoAprobado(aprobado ? solicitud.getMonto() : null);
                    resultado.setTasaAsignada(aprobado ? 0.25 : 0); // TEA 25% para microempresa
                    resultado.setNivelRiesgo(solicitud.getNivelRiesgo());
                    resultado.setMotivoRechazo(aprobado ? null : "Score insuficiente o monto excedido para microempresa");

                    exchange.getIn().setBody(resultado);
                })
                .log("<<< Evaluacion microfinanzas completada: ${body}");

        // --- Evaluacion gerencia (hipotecario) ---
        from("direct:evaluacionGerencia")
                .routeId("evaluacionGerencia")
                .log("Evaluacion GERENCIA para solicitud ${body.id}")
                .process(exchange -> {
                    var solicitud = exchange.getIn().getBody(
                            com.middleware.creditos.model.SolicitudCredito.class);

                    var resultado = new com.middleware.creditos.model.ResultadoEvaluacion();
                    resultado.setSolicitudId(solicitud.getId());
                    // Hipotecario: requiere score alto
                    boolean aprobado = solicitud.getScoreCrediticio() >= 700;
                    resultado.setAprobado(aprobado);
                    resultado.setMontoAprobado(aprobado ? solicitud.getMonto() : null);
                    resultado.setTasaAsignada(aprobado ? 0.12 : 0); // TEA 12% hipotecario
                    resultado.setNivelRiesgo(solicitud.getNivelRiesgo());
                    resultado.setMotivoRechazo(aprobado ? null : "Score insuficiente para credito hipotecario");

                    exchange.getIn().setBody(resultado);
                })
                .log("<<< Evaluacion gerencia completada: ${body}");

        // --- Evaluacion intermedia ---
        from("direct:evaluacionIntermedia")
                .routeId("evaluacionIntermedia")
                .log("Evaluacion INTERMEDIA para solicitud ${body.id}")
                .to("direct:aprobacionAutomatica"); // Reutiliza logica

        // --- Revision manual ---
        from("direct:revisionManual")
                .routeId("revisionManual")
                .log("Solicitud enviada a REVISION MANUAL: ${body.id}")
                .process(exchange -> {
                    var solicitud = exchange.getIn().getBody(
                            com.middleware.creditos.model.SolicitudCredito.class);

                    var resultado = new com.middleware.creditos.model.ResultadoEvaluacion();
                    resultado.setSolicitudId(solicitud.getId());
                    resultado.setAprobado(false);
                    resultado.setMotivoRechazo("Requiere revision manual por analista de creditos");
                    resultado.setNivelRiesgo(solicitud.getNivelRiesgo());

                    exchange.getIn().setBody(resultado);
                });
    }

    /**
     * Determina la TEA (Tasa Efectiva Anual) segun el score.
     * A mayor score, menor tasa (menor riesgo = menor costo).
     */
    private double determinarTasa(int score) {
        if (score >= 750) return 0.15; // 15% TEA - excelente
        if (score >= 650) return 0.20; // 20% TEA - bueno
        if (score >= 550) return 0.28; // 28% TEA - regular
        return 0.35;                    // 35% TEA - riesgo alto
    }
}

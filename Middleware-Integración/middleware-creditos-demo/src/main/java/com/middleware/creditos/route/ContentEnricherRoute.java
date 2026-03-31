package com.middleware.creditos.route;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  CONTENT ENRICHER - Enriquecer mensajes con datos externos       ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PATRON EIP: Content Enricher (Enriquecedor de contenido)
 *
 * Agrega informacion de una fuente externa al mensaje que viaja
 * por la ruta, sin perder los datos originales.
 *
 * CASO DE USO FINANCIERO:
 * Una solicitud de credito llega con datos basicos del cliente.
 * Antes de evaluarla, necesitamos enriquecerla con:
 * 1. Score crediticio (de central de riesgo: Sentinel/Equifax)
 * 2. Historial en el banco (de Bantotal: BTClientes.ObtenerDatos)
 * 3. Verificacion en listas negras (de base de datos interna)
 *
 * TIPOS DE ENRICH:
 * - enrich(): para fuentes "bajo demanda" (HTTP, direct, JMS request-reply)
 *   → Llama al servicio y espera respuesta
 *
 * - pollEnrich(): para fuentes tipo "polling" (file, FTP)
 *   → Lee un recurso que necesita ser consultado (no empujado)
 *
 * AGGREGATION STRATEGY:
 * El enrich necesita saber COMO combinar el mensaje original
 * con la respuesta del enrichment. Para eso se usa una
 * AggregationStrategy que recibe ambos Exchanges y devuelve
 * el resultado combinado.
 */
@Component
public class ContentEnricherRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ============================================================
        // RUTA: Evaluar solicitud con enrichment de multiples fuentes
        // ============================================================
        from("direct:evaluarConEnrichment")
                .routeId("evaluarConEnrichment")
                .log("Iniciando evaluacion con enrichment para: ${body}")

                // ============================================================
                // ENRICH 1: Consultar score crediticio
                //
                // .enrich(uri, aggregationStrategy)
                //
                // uri = endpoint que provee los datos adicionales.
                //   "direct:consultarScore" es una ruta interna que
                //   simula la consulta a la central de riesgo.
                //
                // aggregationStrategy = como combinar original + enrichment.
                //   Recibe 2 Exchanges: el original y la respuesta del enrich.
                //   Debe devolver el Exchange combinado.
                // ============================================================
                .enrich("direct:consultarScore", new AggregationStrategy() {
                    @Override
                    public Exchange aggregate(Exchange original, Exchange enrichment) {
                        // 'original' contiene la SolicitudCredito
                        // 'enrichment' contiene el score de la central de riesgo

                        if (enrichment == null) {
                            // Si el enrich falla o no devuelve nada,
                            // devolvemos el original sin cambios
                            return original;
                        }

                        // Extraer score del enrichment y agregarlo al original
                        int score = enrichment.getIn().getBody(Integer.class);
                        original.getIn().setHeader("scoreCrediticio", score);

                        return original; // Devolvemos el original enriquecido
                    }
                })
                .log("Score crediticio obtenido: ${header.scoreCrediticio}")

                // ============================================================
                // ENRICH 2: Consultar historial en Bantotal
                // ============================================================
                .enrich("direct:consultarHistorialBT", new AggregationStrategy() {
                    @Override
                    public Exchange aggregate(Exchange original, Exchange enrichment) {
                        if (enrichment == null) return original;

                        // Agregar datos del historial como headers
                        String historial = enrichment.getIn().getBody(String.class);
                        original.getIn().setHeader("historialBantotal", historial);

                        return original;
                    }
                })
                .log("Historial Bantotal obtenido: ${header.historialBantotal}")

                .log("Evaluacion con enrichment completada");

        // ============================================================
        // SUB-RUTA: Simula consulta de score crediticio
        // En produccion seria una llamada HTTP a Sentinel/Equifax
        // ============================================================
        from("direct:consultarScore")
                .routeId("consultarScoreExterno")
                .log("Consultando score crediticio...")
                .process(exchange -> {
                    // SIMULACION: en produccion seria algo como:
                    // exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                    // .to("http://sentinel.com.pe/api/score")
                    int scoreSimulado = 500 + (int) (Math.random() * 350);
                    exchange.getIn().setBody(scoreSimulado);
                });

        // ============================================================
        // SUB-RUTA: Simula consulta de historial en Bantotal
        // En produccion llamaria a BTClientes.ObtenerDatos
        // ============================================================
        from("direct:consultarHistorialBT")
                .routeId("consultarHistorialBantotal")
                .log("Consultando historial en Bantotal...")
                .process(exchange -> {
                    // SIMULACION: en produccion seria una llamada a BTServices
                    // con Btinreq + clienteUId
                    exchange.getIn().setBody("CLIENTE_ACTIVO_3_CREDITOS_SIN_MORA");
                });
    }
}

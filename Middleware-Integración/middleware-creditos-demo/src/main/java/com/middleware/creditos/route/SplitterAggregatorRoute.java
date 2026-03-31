package com.middleware.creditos.route;

import com.middleware.creditos.strategy.RespuestaAgregacionStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  SPLITTER + AGGREGATOR - Dividir, procesar, reagrupar            ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PATRONES EIP: Splitter y Aggregator (usados juntos)
 *
 * SPLITTER: Divide un mensaje compuesto en partes individuales
 * y procesa cada parte por separado.
 *
 * AGGREGATOR: Combina multiples mensajes individuales en uno solo,
 * usando una clave de correlacion y una estrategia de agregacion.
 *
 * CASO DE USO FINANCIERO:
 * Proceso batch de desembolsos masivos: llega un archivo CSV con
 * 500 desembolsos. Se splitea en 500 items, se procesa cada uno
 * llamando a Bantotal, y se agrega un resumen final con el total
 * de exitosos y fallidos.
 *
 * OPCIONES IMPORTANTES DEL SPLITTER:
 * - parallelProcessing(): procesar items en paralelo (mas rapido)
 * - streaming(): no cargar todo en memoria (esencial para lotes grandes)
 * - stopOnException(): detener si un item falla (o continuar con los demas)
 *
 * Flujo:
 * CSV con 500 items → SPLIT → 500 Exchange individuales
 *   → Procesar cada uno → AGGREGATE → Resumen consolidado
 */
@Component
public class SplitterAggregatorRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ============================================================
        // RUTA: Procesar lote de pagos (ejemplo con Lista Java)
        //
        // Recibe una List<Map> y procesa cada item individualmente.
        // ============================================================
        from("direct:procesarLotePagos")
                .routeId("splitterPagos")
                .log("Recibido lote de pagos con ${body.size()} items")

                // ============================================================
                // SPLIT: Dividir la lista en items individuales
                //
                // .split(body()) → si el body es una List/Array, crea un
                //   Exchange por cada elemento de la lista.
                //
                // .split(simple("${body.pagos}")) → splitea por un campo
                //   especifico del body (si es un objeto complejo).
                //
                // .split().tokenize(",") → splitea un String por separador.
                //
                // Dentro del split, ${body} es CADA ITEM individual,
                // no la lista completa.
                // ============================================================
                .split(body())

                // parallelProcessing: procesar items en hilos separados.
                // MUY IMPORTANTE para lotes grandes: 500 items secuenciales
                // toman mucho mas que 500 items en paralelo.
                // CUIDADO: Bantotal puede tener limites de concurrencia.
                .parallelProcessing()

                // streaming: no cargar toda la lista en memoria.
                // Esencial cuando el lote tiene miles de items.
                // Sin streaming, Camel carga TODO antes de procesar.
                .streaming()

                // Cada item pasa por esta sub-ruta
                .log("Procesando item individual: ${body}")
                .process(exchange -> {
                    // Cada item es un Map con datos del pago
                    @SuppressWarnings("unchecked")
                    Map<String, Object> pago = exchange.getIn().getBody(Map.class);

                    // Simulacion de procesamiento
                    // En produccion: llamar a BTOperaciones.CrearDebito
                    String resultado = "EXITOSO";
                    if (pago.get("monto") != null) {
                        double monto = Double.parseDouble(pago.get("monto").toString());
                        if (monto > 100000) {
                            resultado = "REQUIERE_APROBACION";
                        }
                    }

                    pago.put("resultado", resultado);
                    exchange.getIn().setBody(pago);
                })
                .log("Item procesado: resultado=${body}")
                .end() // FIN del split
                // NOTA: despues del .end(), el body vuelve a ser la lista original.

                .log("Lote de pagos procesado completamente");

        // ============================================================
        // RUTA: Scatter-Gather con Aggregator
        //
        // PATRON: Scatter-Gather = Multicast + Aggregator
        //
        // Envia la solicitud a MULTIPLES centrales de riesgo en paralelo
        // y AGREGA las respuestas para tomar la mejor decision.
        //
        // Ejemplo: consultar score en Sentinel, Equifax y Experian
        // simultaneamente y quedarse con el promedio o el mejor.
        // ============================================================
        from("direct:consultarMultiplesCentrales")
                .routeId("scatterGatherCentrales")
                .log("Consultando multiples centrales de riesgo...")

                // ============================================================
                // MULTICAST: Enviar el MISMO mensaje a multiples destinos
                //
                // A diferencia del recipientList, los destinos son fijos.
                // parallelProcessing() = consultar las 3 centrales al mismo
                // tiempo (no esperar una para llamar a la otra).
                //
                // AggregationStrategy: como combinar las 3 respuestas en una.
                // Ver RespuestaAgregacionStrategy.java
                // ============================================================
                .multicast(new RespuestaAgregacionStrategy())
                .parallelProcessing()
                .to("direct:centralRiesgoA",
                        "direct:centralRiesgoB",
                        "direct:centralRiesgoC")
                .end() // FIN del multicast

                .log("Respuesta consolidada de centrales: ${body}");

        // --- Simulacion de centrales de riesgo ---
        from("direct:centralRiesgoA")
                .routeId("centralRiesgoA")
                .process(exchange -> {
                    exchange.getIn().setBody(Map.of(
                            "central", "SENTINEL",
                            "score", 720,
                            "deudas", 2
                    ));
                });

        from("direct:centralRiesgoB")
                .routeId("centralRiesgoB")
                .process(exchange -> {
                    exchange.getIn().setBody(Map.of(
                            "central", "EQUIFAX",
                            "score", 680,
                            "deudas", 3
                    ));
                });

        from("direct:centralRiesgoC")
                .routeId("centralRiesgoC")
                .process(exchange -> {
                    exchange.getIn().setBody(Map.of(
                            "central", "EXPERIAN",
                            "score", 700,
                            "deudas", 2
                    ));
                });
    }
}

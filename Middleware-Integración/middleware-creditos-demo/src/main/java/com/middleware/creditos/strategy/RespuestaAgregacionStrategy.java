package com.middleware.creditos.strategy;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  RESPUESTA AGREGACION STRATEGY                                   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Implementa AggregationStrategy para combinar respuestas de
 * multiples centrales de riesgo en una sola lista.
 *
 * AggregationStrategy es la interfaz que Camel usa para saber
 * COMO combinar mensajes en los patrones:
 * - Aggregator
 * - Multicast
 * - Enrich
 * - Splitter (con agregacion)
 *
 * El metodo aggregate() se invoca N veces:
 * - 1ra vez: oldExchange es NULL, newExchange es la 1ra respuesta
 * - 2da vez: oldExchange tiene el resultado acumulado, newExchange es la 2da
 * - N-esima vez: igual, acumulando
 *
 * CASO DE USO:
 * Consultar score en 3 centrales de riesgo → agregar las 3 respuestas
 * en una lista para luego tomar la decision con toda la informacion.
 */
public class RespuestaAgregacionStrategy implements AggregationStrategy {

    /**
     * @param oldExchange - El Exchange acumulado (NULL en la primera llamada)
     * @param newExchange - El nuevo Exchange a agregar
     * @return Exchange con el resultado combinado
     */
    @Override
    @SuppressWarnings("unchecked")
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        // PRIMERA LLAMADA: oldExchange es null
        // Inicializamos la lista con el primer resultado
        if (oldExchange == null) {
            List<Map<String, Object>> resultados = new ArrayList<>();
            resultados.add(newExchange.getIn().getBody(Map.class));
            newExchange.getIn().setBody(resultados);
            return newExchange;
        }

        // LLAMADAS SIGUIENTES: agregar el nuevo resultado a la lista
        List<Map<String, Object>> resultados = oldExchange.getIn().getBody(List.class);
        resultados.add(newExchange.getIn().getBody(Map.class));
        oldExchange.getIn().setBody(resultados);

        return oldExchange;
    }
}

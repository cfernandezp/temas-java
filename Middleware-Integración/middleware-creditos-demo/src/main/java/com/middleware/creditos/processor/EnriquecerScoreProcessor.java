package com.middleware.creditos.processor;

import com.middleware.creditos.model.SolicitudCredito;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  ENRIQUECER SCORE PROCESSOR                                      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Simula la consulta a una central de riesgo (Sentinel, Equifax)
 * para obtener el score crediticio del cliente.
 *
 * En PRODUCCION este processor llamaria a un servicio externo:
 * - Sentinel (Peru): consulta historial crediticio
 * - Equifax: score y deudas
 * - SBS: clasificacion de riesgo actual
 *
 * El score crediticio es un numero entre 300 y 850:
 * - 300-499: Alto riesgo (probable rechazo o tasa alta)
 * - 500-649: Riesgo medio (evaluacion manual)
 * - 650-749: Buen perfil (aprobacion probable)
 * - 750-850: Excelente (aprobacion automatica, mejor tasa)
 *
 * Este processor se usa dentro del patron CONTENT ENRICHER
 * de la ruta ContentEnricherRoute.
 */
@Component("enriquecerScoreProcessor")
public class EnriquecerScoreProcessor implements Processor {

    // Simulacion: en produccion esto seria un client HTTP
    // que llama a la API de Sentinel/Equifax
    private final Random random = new Random();

    @Override
    public void process(Exchange exchange) throws Exception {

        SolicitudCredito solicitud = exchange.getIn().getBody(SolicitudCredito.class);

        // --- SIMULACION: Generar score aleatorio ---
        // En produccion:
        // String response = httpClient.get("https://sentinel.com.pe/api/score/" + solicitud.getDniCliente());
        // int score = parseScore(response);
        int score = 400 + random.nextInt(450); // Score entre 400 y 850

        // Asignar el score a la solicitud
        solicitud.setScoreCrediticio(score);

        // Determinar nivel de riesgo segun clasificacion SBS
        // La SBS (Superintendencia de Banca y Seguros) define 5 categorias:
        String nivelRiesgo = clasificarRiesgoSBS(score);
        solicitud.setNivelRiesgo(nivelRiesgo);

        // Actualizar el body con la solicitud enriquecida
        exchange.getIn().setBody(solicitud);

        // Headers informativos para logging y decision posterior
        exchange.getIn().setHeader("scoreCrediticio", score);
        exchange.getIn().setHeader("nivelRiesgo", nivelRiesgo);
    }

    /**
     * Clasifica el riesgo segun normativa SBS peruana.
     *
     * NORMAL     = cliente al dia, sin atrasos
     * CPP        = Con Problemas Potenciales (atrasos 9-30 dias)
     * DEFICIENTE = atrasos 31-60 dias
     * DUDOSO     = atrasos 61-120 dias
     * PERDIDA    = atrasos mayor a 120 dias (credito irrecuperable)
     *
     * Cada clasificacion tiene un porcentaje de provision obligatorio:
     * NORMAL=1%, CPP=5%, DEFICIENTE=25%, DUDOSO=50%, PERDIDA=100%
     */
    private String clasificarRiesgoSBS(int score) {
        if (score >= 700) return "NORMAL";
        if (score >= 600) return "CPP";
        if (score >= 500) return "DEFICIENTE";
        if (score >= 400) return "DUDOSO";
        return "PERDIDA";
    }
}

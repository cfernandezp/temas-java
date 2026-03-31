package com.middleware.creditos.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  AUDITAR OPERACION PROCESSOR                                     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Registra informacion de auditoria para cada operacion financiera.
 *
 * En el sector financiero, la auditoria es OBLIGATORIA por regulacion.
 * Cada operacion debe registrar:
 * - Quien la hizo (usuario, canal)
 * - Cuando (timestamp)
 * - Que operacion fue (tipo, ID)
 * - Desde donde (IP, dispositivo)
 * - Resultado (exito o error)
 *
 * Este processor se usa tipicamente con el patron WIRE TAP:
 * .wireTap("seda:auditoria").newExchangeProcessor(auditarOperacionProcessor)
 *
 * Wire Tap envia una COPIA del mensaje a auditoria sin afectar
 * el flujo principal. El usuario no espera a que se grabe la auditoria.
 */
@Component("auditarOperacionProcessor")
public class AuditarOperacionProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(AuditarOperacionProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        // Construir registro de auditoria desde headers y body del Exchange

        String operacion = exchange.getIn().getHeader("operationType", "DESCONOCIDA", String.class);
        String solicitudId = exchange.getIn().getHeader("solicitudId", "N/A", String.class);
        String usuario = exchange.getIn().getHeader("usuario", "SISTEMA", String.class);
        String canal = exchange.getIn().getHeader("canal", "MIDDLEWARE", String.class);
        String ipOrigen = exchange.getIn().getHeader("X-Forwarded-For", "127.0.0.1", String.class);

        // Exchange ID es unico para cada mensaje que viaja por Camel.
        // Sirve como correlation ID para rastrear el flujo completo.
        String exchangeId = exchange.getExchangeId();

        // Timestamp en formato ISO-8601 para estandarizacion
        String timestamp = Instant.now().toString();

        // ---- En produccion esto se guardaria en: ----
        // 1. Base de datos de auditoria (tabla audit_log)
        // 2. Kafka topic "auditoria-operaciones" (para analisis en tiempo real)
        // 3. Elasticsearch (para busqueda y dashboards en Kibana)

        // Para este demo, logueamos la auditoria
        log.info("AUDITORIA | timestamp={} | operacion={} | solicitudId={} | " +
                        "usuario={} | canal={} | ip={} | exchangeId={}",
                timestamp, operacion, solicitudId, usuario, canal, ipOrigen, exchangeId);

        // Setear headers de auditoria para que esten disponibles
        // en pasos posteriores de la ruta (si los hay)
        exchange.getIn().setHeader("audit-timestamp", timestamp);
        exchange.getIn().setHeader("audit-exchangeId", exchangeId);
    }
}

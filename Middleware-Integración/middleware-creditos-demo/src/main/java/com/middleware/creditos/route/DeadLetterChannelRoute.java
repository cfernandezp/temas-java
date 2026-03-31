package com.middleware.creditos.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  DEAD LETTER CHANNEL - Manejo robusto de errores                 ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PATRON EIP: Dead Letter Channel (Canal de mensajes muertos)
 *
 * Cuando un mensaje falla despues de N reintentos, en vez de
 * perderse, se envia a una cola especial (DLQ) para revision
 * posterior y reprocesamiento manual.
 *
 * En el sector financiero, NUNCA puedes perder un mensaje.
 * Si una transferencia falla, no puedes simplemente descartarla.
 * Debe quedar registrada en la DLQ para que un operador la revise.
 *
 * JERARQUIA DE ERROR HANDLING EN CAMEL:
 * 1. doTry/doCatch   → try-catch local en una ruta (mas especifico)
 * 2. onException     → handler por tipo de excepcion (medio)
 * 3. errorHandler    → handler global para toda la ruta (mas general)
 *
 * Camel busca el handler mas especifico primero.
 * Si hay un onException para esa excepcion, lo usa.
 * Si no, usa el errorHandler global.
 *
 * REDELIVERY (reintentos):
 * - maximumRedeliveries: cuantas veces reintenta antes de enviar a DLQ
 * - redeliveryDelay: tiempo entre reintentos (en ms)
 * - useExponentialBackOff: cada reintento espera mas (2s, 4s, 8s...)
 * - backOffMultiplier: multiplicador del backoff (2 = duplica cada vez)
 */
@Component
public class DeadLetterChannelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ============================================================
        // ERROR HANDLER GLOBAL: Dead Letter Channel
        //
        // Se aplica a TODAS las rutas definidas en este RouteBuilder.
        // Si quieres un handler diferente por ruta, usa onException.
        //
        // "jms:queue:DLQ.creditos" = cola JMS donde van los mensajes
        // que fallan. En produccion esta cola la monitorea un equipo
        // de soporte que reprocesa o investiga los errores.
        // ============================================================
        errorHandler(
                deadLetterChannel("jms:queue:DLQ.creditos")
                        // Reintentar 3 veces antes de enviar a DLQ
                        .maximumRedeliveries(3)

                        // Esperar 2 segundos entre reintentos
                        .redeliveryDelay(2000)

                        // Backoff exponencial: 2s → 4s → 8s
                        // Esto evita saturar un servicio que esta caido
                        .useExponentialBackOff()
                        .backOffMultiplier(2)

                        // Nivel de log para reintentos
                        .retryAttemptedLogLevel(LoggingLevel.WARN)

                        // Log cuando se envia a DLQ
                        .logExhausted(true)
                        .logExhaustedMessageHistory(true)
        );

        // ============================================================
        // onException: Handlers por tipo de excepcion
        //
        // Mas especificos que el errorHandler global.
        // Puedes definir comportamiento diferente para cada tipo de error.
        // ============================================================

        // --- Errores de validacion: NO reintentar ---
        // Si los datos son invalidos, reintentar no va a arreglar nada.
        // Devolvemos error 400 inmediatamente.
        onException(IllegalArgumentException.class)
                .handled(true)
                // handled(true): Camel considera el error como "manejado"
                // y NO lo propaga al errorHandler global.
                // El mensaje NO va a la DLQ.
                .maximumRedeliveries(0) // Sin reintentos
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("{\"error\": \"Validacion: ${exception.message}\"}"))
                .log(LoggingLevel.WARN, "Validacion fallida (sin reintento): ${exception.message}");

        // --- Errores de conexion: Reintentar MAS veces ---
        // Los errores de red suelen ser transitorios.
        // Reintentamos mas agresivamente.
        onException(java.net.ConnectException.class)
                .maximumRedeliveries(5)
                .redeliveryDelay(5000)
                .useExponentialBackOff()
                .backOffMultiplier(2)
                .log(LoggingLevel.ERROR, "Error de conexion (reintentando): ${exception.message}");
        // NOTA: No ponemos handled(true), asi que si despues de 5 reintentos
        // sigue fallando, el errorHandler global lo envia a la DLQ.

        // ============================================================
        // RUTA: Procesa un pago (demuestra manejo de errores)
        // ============================================================
        from("direct:procesarPago")
                .routeId("procesarPagoConErrorHandling")
                .log("Procesando pago: ${body}")

                // doTry/doCatch: try-catch INLINE en la ruta
                // Mas especifico que onException, se aplica solo
                // a los pasos dentro del doTry.
                .doTry()
                .log("Intentando procesar pago...")
                .process(exchange -> {
                    // Simulacion: falla aleatoriamente para demostrar error handling
                    if (Math.random() < 0.3) {
                        throw new RuntimeException("Error simulado al procesar pago");
                    }
                    exchange.getIn().setBody("{\"status\": \"PAGO_PROCESADO\", \"message\": \"Exito\"}");
                })
                .doCatch(RuntimeException.class)
                // doCatch captura la excepcion ANTES de que llegue al onException
                .log(LoggingLevel.WARN, "Error capturado en doTry: ${exception.message}")
                .setBody(simple("{\"status\": \"ERROR\", \"message\": \"${exception.message}\"}"))
                .doFinally()
                // doFinally se ejecuta SIEMPRE (exito o error)
                .log("Procesamiento de pago finalizado (finally)")
                .end();

        // ============================================================
        // RUTA: Monitorea la DLQ (Dead Letter Queue)
        //
        // En produccion, esta ruta alertaria al equipo de soporte
        // cuando un mensaje llega a la DLQ.
        // ============================================================
        from("jms:queue:DLQ.creditos")
                .routeId("monitorDLQ")
                .log(LoggingLevel.ERROR,
                        "ALERTA DLQ | Mensaje en Dead Letter Queue | " +
                                "Exchange: ${exchangeId} | Causa: ${exception.message}")
                // En produccion:
                // .to("http://alertas-service/api/notificar")
                // .to("kafka:alertas-dlq")
                .log("DLQ | Mensaje registrado para reprocesamiento");
    }
}

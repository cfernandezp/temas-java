package com.middleware.creditos.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  IDEMPOTENT CONSUMER - Evitar procesar mensajes duplicados       ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PATRON EIP: Idempotent Consumer (Consumidor idempotente)
 *
 * Garantiza que un mensaje se procese UNA SOLA VEZ, aunque llegue
 * multiples veces. Usa una clave unica (ID de transaccion) y un
 * repositorio para recordar que ya fue procesado.
 *
 * POR QUE ES CRITICO EN FINANZAS?
 * Imagina que un pago de S/ 5,000 llega duplicado por un retry
 * del broker JMS. Sin idempotencia, se debita dos veces la cuenta.
 * Con idempotencia, el segundo mensaje se detecta como duplicado
 * y se descarta.
 *
 * COMO FUNCIONA:
 * 1. Llega un mensaje con ID = "TX-12345"
 * 2. Camel busca "TX-12345" en el repositorio
 * 3. Si NO existe: lo procesa y registra el ID
 * 4. Si YA existe: lo descarta (es duplicado)
 *
 * TIPOS DE REPOSITORIO:
 * - MemoryIdempotentRepository: en memoria (se pierde al reiniciar)
 *   → Solo para demos y desarrollo
 * - JdbcMessageIdRepository: en base de datos (persistente)
 *   → PRODUCCION: tabla en la BD con los IDs procesados
 * - InfinispanIdempotentRepository: cache distribuido
 *   → Para clusters de middleware en OpenShift
 *
 * En produccion SIEMPRE usa JDBC o Infinispan, nunca memoria.
 */
@Component
public class IdempotentConsumerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ============================================================
        // RUTA: Consumidor idempotente de pagos
        //
        // Lee pagos de la cola JMS y los procesa garantizando
        // que cada pago se ejecute UNA SOLA VEZ.
        // ============================================================
        from("jms:queue:pagos-entrantes")
                .routeId("pagoIdempotente")
                .log("Pago recibido de cola JMS: ${body}")

                // ============================================================
                // IDEMPOTENT CONSUMER
                //
                // Parametro 1: expresion que extrae la CLAVE UNICA del mensaje.
                //   header("idTransaccion") → usa el header JMS "idTransaccion"
                //   Cada pago debe tener un ID unico asignado por el canal.
                //
                // Parametro 2: repositorio donde se guardan los IDs procesados.
                //   MemoryIdempotentRepository(200) → guarda hasta 200 IDs
                //   en memoria. Se pierden al reiniciar la app.
                //
                //   EN PRODUCCION USAR:
                //   new JdbcMessageIdRepository(dataSource, "PROCESSED_MESSAGES")
                //   Esto guarda los IDs en una tabla SQL persistente.
                // ============================================================
                .idempotentConsumer(
                        header("idTransaccion"),
                        MemoryIdempotentRepository.memoryIdempotentRepository(200)
                )
                // skipDuplicate(true) = descarta silenciosamente los duplicados (default)
                // skipDuplicate(false) = los pasa pero con header "CamelDuplicateMessage"=true
                .skipDuplicate(true)

                .log("Procesando pago (no duplicado): ID=${header.idTransaccion}")

                // Logica de procesamiento del pago
                .process(exchange -> {
                    String idTx = exchange.getIn().getHeader("idTransaccion", String.class);
                    String body = exchange.getIn().getBody(String.class);

                    // Aqui iria la logica real:
                    // 1. Validar fondos en Bantotal
                    // 2. Ejecutar debito con BTOperaciones.CrearDebito
                    // 3. Registrar en contabilidad

                    exchange.getIn().setBody(
                            "{\"status\": \"PROCESADO\", \"idTransaccion\": \"" + idTx + "\"}");
                })

                .log("Pago procesado exitosamente: ${body}");

        /*
         * ============================================================
         * EJEMPLO CON JDBC REPOSITORY (para produccion):
         *
         * En produccion, el repositorio debe ser persistente.
         * JdbcMessageIdRepository guarda los IDs en una tabla SQL:
         *
         * CREATE TABLE PROCESSED_MESSAGES (
         *     processorName VARCHAR(255),
         *     messageId     VARCHAR(255),
         *     createdAt     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         *     PRIMARY KEY (processorName, messageId)
         * );
         *
         * Configuracion:
         * @Autowired DataSource dataSource;
         *
         * .idempotentConsumer(
         *     header("idTransaccion"),
         *     new JdbcMessageIdRepository(dataSource, "pagoProcessor")
         * )
         *
         * Esto sobrevive reinicios del middleware y funciona
         * en clusters (multiples pods en OpenShift acceden a la misma BD).
         * ============================================================
         */
    }
}

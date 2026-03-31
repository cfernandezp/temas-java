package com.middleware.creditos.route;

import com.middleware.creditos.model.BantotalResponse;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  BANTOTAL INTEGRATION ROUTE - Integracion con core bancario      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Ruta que orquesta la comunicacion con BTServices de Bantotal.
 * Demuestra:
 * - Transformacion de datos (modelo interno → formato Bantotal)
 * - Construccion del Btinreq (cabecera obligatoria)
 * - Llamada HTTP/REST a BTServices
 * - Manejo de errores de negocio de Bantotal
 * - Circuit breaker para proteger al core
 *
 * FLUJO COMPLETO DE SIMULACION DE CREDITO:
 * 1. Recibir solicitud del canal (REST API)
 * 2. Validar datos basicos (ValidarSolicitudProcessor)
 * 3. Transformar al formato Bantotal (TransformarBantotalProcessor)
 * 4. Serializar a JSON (marshal)
 * 5. Enviar POST a BTServices (HTTP)
 * 6. Deserializar respuesta (unmarshal)
 * 7. Verificar errores de negocio
 * 8. Devolver resultado al canal
 *
 * EN PRODUCCION:
 * La URL de Bantotal seria algo como:
 * http://bantotal-core.banco.internal:8080/BTServices/api/Prestamos/Simular
 *
 * En este demo, usamos un simulador local (BantotalSimulatorRoute)
 * que devuelve respuestas realistas sin necesitar un Bantotal real.
 */
@Component
public class BantotalIntegrationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ============================================================
        // MANEJO DE ERRORES ESPECIFICO PARA BANTOTAL
        // ============================================================

        // Errores de negocio de Bantotal (saldo insuficiente, cliente no encontrado)
        // No reintentar porque no tiene sentido.
        onException(BantotalNegocioException.class)
                .handled(true)
                .maximumRedeliveries(0)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
                .setBody(simple("{\"error\": \"Error de negocio Bantotal: ${exception.message}\"}"))
                .log(LoggingLevel.WARN, "Error de negocio BT: ${exception.message}");

        // ============================================================
        // RUTA: Simular credito en Bantotal
        // ============================================================
        from("direct:simularBantotal")
                .routeId("simularCreditoBantotal")
                .log(">>> Simulando credito en Bantotal")

                // PASO 1: Transformar solicitud al formato Bantotal
                // El processor construye el BantotalRequest con Btinreq + payload
                .process("transformarBantotalProcessor")
                .log("Request BT construido: ${body}")

                // PASO 2: Serializar a JSON
                // marshal().json() convierte el objeto Java a String JSON.
                // Bantotal espera el body como JSON en la peticion POST.
                .marshal().json()

                // PASO 3: Setear headers HTTP para la llamada REST
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))

                // ============================================================
                // PASO 4: Llamar a BTServices
                //
                // EN PRODUCCION usarias:
                // .to("http://{{bantotal.base-url}}/BTServices/api/Prestamos/Simular"
                //     + "?connectTimeout=5000"
                //     + "&socketTimeout={{bantotal.timeout}}")
                //
                // NOTAS sobre el componente camel-http:
                // - connectTimeout: tiempo maximo para establecer conexion TCP
                // - socketTimeout: tiempo maximo esperando respuesta
                // - throwExceptionOnFailure=false: no lanzar excepcion en 4xx/5xx
                //   (util si quieres manejar los errores HTTP tu mismo)
                //
                // En este demo usamos el simulador local:
                // ============================================================
                .to("direct:bantotalSimulador")

                // PASO 5: Deserializar respuesta JSON a objeto Java
                .unmarshal().json(BantotalResponse.class)

                // PASO 6: Verificar errores de negocio de Bantotal
                .process(exchange -> {
                    BantotalResponse response = exchange.getIn().getBody(BantotalResponse.class);

                    // IMPORTANTE: Bantotal puede devolver HTTP 200 pero con
                    // errores de negocio en el campo "Erroresnegocio".
                    // SIEMPRE verificar este campo.
                    if (response.tieneErrores()) {
                        String error = response.getErroresnegocio().get(0).get("Descripcion");
                        String codigo = response.getErroresnegocio().get(0).get("Codigo");
                        throw new BantotalNegocioException(
                                "Codigo " + codigo + ": " + error);
                    }

                    // Si no hay errores, devolver el payload de la simulacion
                    exchange.getIn().setBody(response.getPayload());
                })

                .log("<<< Simulacion Bantotal exitosa: ${body}");

        // ============================================================
        // RUTA: Orquestacion de desembolso (multiples llamadas a BT)
        //
        // PATRON: Saga / Orquestacion secuencial con compensacion
        //
        // Un desembolso de credito requiere multiples pasos en orden.
        // Si un paso falla, se deben COMPENSAR los pasos anteriores.
        // ============================================================
        from("direct:orquestacionDesembolso")
                .routeId("orquestacionDesembolso")
                .log("Iniciando orquestacion de desembolso")

                // Wire Tap para auditoria de inicio
                .wireTap("seda:auditoriaOperaciones")

                // Paso 1: Validar cliente en Bantotal
                .to("direct:btValidarCliente")
                .log("Paso 1 OK: Cliente validado")

                // Paso 2: Simular prestamo
                .to("direct:simularBantotal")
                .log("Paso 2 OK: Simulacion completada")

                // Paso 3: Crear prestamo (en produccion: BTPrestamos.Crear)
                .to("direct:btCrearPrestamo")
                .log("Paso 3 OK: Prestamo creado")

                // Paso 4: Acreditar monto en cuenta
                .to("direct:btAcreditarCuenta")
                .log("Paso 4 OK: Monto acreditado")

                // Notificar al cliente (asincrono)
                .wireTap("seda:notificaciones")

                .log("<<< Desembolso completado exitosamente");

        // --- Sub-rutas simuladas de Bantotal ---
        from("direct:btValidarCliente")
                .routeId("btValidarCliente")
                .log("Validando cliente en Bantotal...")
                .setBody(constant("{\"clienteValido\": true}"));

        from("direct:btCrearPrestamo")
                .routeId("btCrearPrestamo")
                .log("Creando prestamo en Bantotal...")
                .setBody(constant("{\"prestamoId\": \"PREST-2026-001\", \"estado\": \"CREADO\"}"));

        from("direct:btAcreditarCuenta")
                .routeId("btAcreditarCuenta")
                .log("Acreditando monto en cuenta...")
                .setBody(constant("{\"acreditacion\": \"OK\", \"operacionId\": \"OP-12345\"}"));
    }

    /**
     * Excepcion para errores de negocio de Bantotal.
     * Se separa de excepciones tecnicas porque el manejo es diferente:
     * - Error de negocio: NO reintentar, devolver al canal
     * - Error tecnico (timeout): SI reintentar con backoff
     */
    public static class BantotalNegocioException extends RuntimeException {
        public BantotalNegocioException(String message) {
            super(message);
        }
    }
}

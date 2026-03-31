package com.middleware.creditos.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  WIRE TAP - Patron de auditoria sin bloquear el flujo            ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PATRON EIP: Wire Tap (Escucha de cable)
 *
 * Envia una COPIA del mensaje a otro destino sin afectar el flujo
 * principal. El flujo principal NO espera a que el wire tap termine.
 *
 * Es como poner una "derivacion" en un cable: el mensaje sigue su
 * camino normal y una copia va a otro lado (auditoria, logging, etc.)
 *
 * USO EN SECTOR FINANCIERO:
 * La regulacion exige registrar TODA operacion financiera.
 * Pero no puedes hacer que el cliente espere mientras se graba
 * la auditoria. Wire Tap resuelve esto perfectamente.
 *
 * IMPORTANTE: Wire Tap crea una SHALLOW COPY del Exchange.
 * Si el body es un objeto mutable y lo modificas en el wire tap,
 * podrias afectar el flujo principal. Para evitarlo, usa
 * .wireTap("destino").copy() o un Processor que cree un nuevo objeto.
 *
 *  Flujo principal:    Solicitud → Validar → Evaluar → Responder (rapido)
 *                                     ↓ (copia)
 *  Wire Tap:           Copia → Registrar auditoria → Guardar en BD/Kafka (asincrono)
 */
@Component
public class WireTapRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ============================================================
        // RUTA: Auditoria asincrona
        //
        // Recibe copias de los mensajes via Wire Tap y las procesa.
        // Usa "seda:" que es asincrono (cola en memoria, otro hilo).
        //
        // seda vs direct:
        // - seda: asincrono, cola en memoria, el productor NO espera
        // - direct: sincrono, mismo hilo, el productor ESPERA
        //
        // Para auditoria usamos seda porque no necesitamos
        // que el flujo principal espere a que se grabe.
        // ============================================================
        from("seda:auditoriaOperaciones")
                .routeId("auditoriaWireTap")
                .log("WIRE TAP | Registrando auditoria para: ${body}")

                // Invocar el processor de auditoria
                .process("auditarOperacionProcessor")

                // En produccion, despues de procesar la auditoria,
                // la enviarias a un destino persistente:
                // .to("kafka:auditoria-transacciones")  ← Kafka topic
                // .to("jms:queue:auditoria")             ← Cola JMS persistente
                // .to("jdbc:dataSource")                  ← Base de datos

                .log("WIRE TAP | Auditoria registrada exitosamente");

        // ============================================================
        // RUTA: Notificaciones asincronas
        //
        // Otra ruta que recibe wire taps para enviar notificaciones
        // al cliente (SMS, push notification, email).
        // ============================================================
        from("seda:notificaciones")
                .routeId("notificacionesAsync")
                .log("NOTIFICACION | Enviando notificacion para: ${header.solicitudId}")
                // En produccion:
                // .to("http://servicio-notificaciones/api/enviar")
                // .to("jms:queue:notificaciones-push")
                .log("NOTIFICACION | Notificacion enviada");
    }
}

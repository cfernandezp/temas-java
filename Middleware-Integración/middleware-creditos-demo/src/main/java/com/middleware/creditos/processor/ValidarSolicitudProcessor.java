package com.middleware.creditos.processor;

import com.middleware.creditos.model.SolicitudCredito;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  VALIDAR SOLICITUD PROCESSOR                                     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PATRON: Processor (componente fundamental de Apache Camel)
 *
 * Un Processor es una unidad de logica que transforma o valida
 * el Exchange (mensaje) que viaja por la ruta. Implementa la
 * interfaz org.apache.camel.Processor con un solo metodo: process().
 *
 * Se usa en las rutas con: .process(new ValidarSolicitudProcessor())
 * O si es un bean Spring: .process("validarSolicitudProcessor")
 *
 * EXCHANGE = objeto que contiene:
 * - In Message: mensaje de entrada (headers + body)
 * - Out Message: mensaje de salida (se usa en patrones InOut)
 * - Properties: datos internos de la ruta (no se propagan entre endpoints)
 * - ExchangeId: ID unico para trazabilidad
 *
 * Este processor valida las reglas de negocio basicas de una
 * solicitud de credito antes de enviarla al core bancario.
 */
@Component("validarSolicitudProcessor") // Nombre del bean para referenciar en rutas
public class ValidarSolicitudProcessor implements Processor {

    // Montos minimo y maximo permitidos (en soles)
    // Estos valores normalmente vendrian de configuracion o del core
    private static final BigDecimal MONTO_MINIMO = new BigDecimal("500");
    private static final BigDecimal MONTO_MAXIMO = new BigDecimal("500000");
    private static final int PLAZO_MINIMO_MESES = 3;
    private static final int PLAZO_MAXIMO_MESES = 72; // 6 anos maximo

    /**
     * Metodo principal del Processor.
     *
     * @param exchange - El Exchange que viaja por la ruta Camel.
     *
     * exchange.getIn().getBody(Class) → obtiene el body casteado al tipo.
     * exchange.getIn().getHeader("nombre") → obtiene un header.
     * exchange.getIn().setBody(objeto) → reemplaza el body.
     * exchange.getIn().setHeader("nombre", valor) → agrega/modifica header.
     *
     * Si lanzamos una excepcion, Camel la maneja segun el errorHandler
     * configurado en la ruta (reintento, DLQ, respuesta de error, etc.)
     */
    @Override
    public void process(Exchange exchange) throws Exception {

        // Obtener el body del Exchange casteado a SolicitudCredito.
        // Camel usa TypeConverters para hacer la conversion.
        // Si el body es un JSON string, primero se debe hacer
        // .unmarshal().json() en la ruta ANTES de este processor.
        SolicitudCredito solicitud = exchange.getIn().getBody(SolicitudCredito.class);

        // ---- Validaciones de negocio ----

        // Validar que la solicitud no sea null
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud de credito no puede ser nula");
        }

        // Validar campos obligatorios
        if (solicitud.getClienteId() == null || solicitud.getClienteId().isBlank()) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio");
        }

        if (solicitud.getDniCliente() == null || solicitud.getDniCliente().isBlank()) {
            throw new IllegalArgumentException("El DNI del cliente es obligatorio");
        }

        // Validar formato DNI peruano (8 digitos)
        if (!solicitud.getDniCliente().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "DNI invalido: debe tener 8 digitos. Recibido: " + solicitud.getDniCliente());
        }

        // Validar monto dentro de rango permitido
        if (solicitud.getMonto() == null) {
            throw new IllegalArgumentException("El monto es obligatorio");
        }

        if (solicitud.getMonto().compareTo(MONTO_MINIMO) < 0) {
            throw new IllegalArgumentException(
                    "Monto minimo es S/ " + MONTO_MINIMO + ". Solicitado: S/ " + solicitud.getMonto());
        }

        if (solicitud.getMonto().compareTo(MONTO_MAXIMO) > 0) {
            throw new IllegalArgumentException(
                    "Monto maximo es S/ " + MONTO_MAXIMO + ". Solicitado: S/ " + solicitud.getMonto());
        }

        // Validar plazo dentro de rango
        if (solicitud.getPlazoMeses() < PLAZO_MINIMO_MESES || solicitud.getPlazoMeses() > PLAZO_MAXIMO_MESES) {
            throw new IllegalArgumentException(
                    "Plazo debe estar entre " + PLAZO_MINIMO_MESES + " y " + PLAZO_MAXIMO_MESES +
                            " meses. Solicitado: " + solicitud.getPlazoMeses());
        }

        // Validar tipo de producto
        if (solicitud.getTipoProducto() == null || solicitud.getTipoProducto().isBlank()) {
            throw new IllegalArgumentException("El tipo de producto es obligatorio");
        }

        // ---- Si pasa todas las validaciones ----

        // Marcar la solicitud como en evaluacion
        solicitud.setEstado("EN_EVALUACION");

        // Setear el body modificado de vuelta al Exchange.
        // Esto es necesario porque el Exchange pasa al siguiente
        // paso de la ruta con este body actualizado.
        exchange.getIn().setBody(solicitud);

        // Agregar un header para trazabilidad.
        // Los headers viajan con el mensaje por toda la ruta
        // y son accesibles en cualquier paso posterior.
        exchange.getIn().setHeader("validacionExitosa", true);
        exchange.getIn().setHeader("solicitudId", solicitud.getId());
    }
}

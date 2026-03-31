package com.middleware.creditos.processor;

import com.middleware.creditos.model.BantotalRequest;
import com.middleware.creditos.model.Btinreq;
import com.middleware.creditos.model.SolicitudCredito;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  TRANSFORMAR A FORMATO BANTOTAL - Processor                      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PATRON EIP: Message Translator (Traductor de Mensajes)
 *
 * Convierte el modelo interno (SolicitudCredito) al formato que
 * espera BTServices de Bantotal (BantotalRequest con Btinreq).
 *
 * Este es uno de los patrones mas usados en middleware: los canales
 * digitales envian datos en un formato, y el core bancario espera
 * otro formato. El middleware traduce entre ambos.
 *
 * Ejemplo del flujo:
 * Canal envia:  { "monto": 50000, "plazoMeses": 12, "clienteId": "C001" }
 * Bantotal espera: { "Btinreq": {...}, "sdtPrestamo": { "monto": 50000, "cantidadCuotas": 12, ... } }
 */
@Component("transformarBantotalProcessor")
public class TransformarBantotalProcessor implements Processor {

    // @Value inyecta valores desde application.yml.
    // En Camel tambien puedes usar {{bantotal.canal}} en las rutas,
    // pero en un Processor usas @Value de Spring.
    @Value("${bantotal.canal}")
    private String canal;

    @Value("${bantotal.usuario}")
    private String usuario;

    @Value("${bantotal.device}")
    private String device;

    @Override
    public void process(Exchange exchange) throws Exception {

        // 1. Obtener la solicitud del body del Exchange
        SolicitudCredito solicitud = exchange.getIn().getBody(SolicitudCredito.class);

        // 2. Construir la cabecera Btinreq
        // UUID.randomUUID() genera un ID unico para este requerimiento.
        // Este ID permite rastrear esta operacion especifica en los
        // logs de Bantotal para auditoria y troubleshooting.
        Btinreq btinreq = new Btinreq();
        btinreq.setCanal(canal);
        btinreq.setUsuario(usuario);
        btinreq.setDevice(device);
        btinreq.setRequerimiento(UUID.randomUUID().toString());

        // El token normalmente se obtiene de un cache o se renueva
        // con un timer. Aqui lo tomamos de un header que se seteo
        // en una ruta anterior (la ruta de autenticacion con BT)
        String token = exchange.getIn().getHeader("bt-token", String.class);
        btinreq.setToken(token != null ? token : "TOKEN-DEMO-12345");

        // 3. Construir el payload en formato Bantotal (sdtPrestamo)
        // Los nombres de campos siguen la convencion de BTServices
        Map<String, Object> sdtPrestamo = new HashMap<>();
        sdtPrestamo.put("productoUId", determinarProductoBT(solicitud.getTipoProducto()));
        sdtPrestamo.put("monto", solicitud.getMonto());
        sdtPrestamo.put("cantidadCuotas", solicitud.getPlazoMeses());
        sdtPrestamo.put("periodoCuotas", 30); // Cuotas mensuales (30 dias)
        sdtPrestamo.put("tasa", 0);           // 0 = Bantotal calcula la tasa segun producto

        // 4. Armar el request completo
        BantotalRequest request = new BantotalRequest();
        request.setBtinreq(btinreq);
        request.setPayload(sdtPrestamo);

        // 5. Reemplazar el body del Exchange con el request de Bantotal.
        // El siguiente paso de la ruta recibira este objeto como body.
        exchange.getIn().setBody(request);

        // 6. Guardar la solicitud original en una Property del Exchange.
        // Properties NO se propagan a endpoints externos (a diferencia de headers).
        // Util para tener acceso a la solicitud original despues de
        // procesar la respuesta de Bantotal.
        exchange.setProperty("solicitudOriginal", solicitud);

        // 7. Guardar el ID de requerimiento en un header para trazabilidad
        exchange.getIn().setHeader("bt-requerimiento", btinreq.getRequerimiento());
    }

    /**
     * Mapea el tipo de producto interno al ID de producto en Bantotal.
     * Cada producto en Bantotal tiene un ID unico (productoUId).
     * Estos IDs se configuran al parametrizar Bantotal.
     */
    private int determinarProductoBT(String tipoProducto) {
        return switch (tipoProducto != null ? tipoProducto.toUpperCase() : "") {
            case "PERSONAL" -> 1001;
            case "MICROEMPRESA" -> 2001;
            case "HIPOTECARIO" -> 3001;
            case "AGRICOLA" -> 4001;
            default -> 1001; // Default a personal
        };
    }
}

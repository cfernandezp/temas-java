package com.middleware.creditos.model;

import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  BANTOTAL REQUEST - Envelope de peticiones a BTServices          ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Toda peticion a BTServices sigue este patron:
 * {
 *   "Btinreq": { ... cabecera ... },
 *   "sdtXxx": { ... datos especificos de la operacion ... }
 * }
 *
 * El prefijo "sdt" (Structured Data Type) es la convencion de
 * Bantotal para estructuras de datos complejas.
 *
 * Este wrapper generico usa un Map para el payload, lo que permite
 * reutilizarlo para cualquier operacion de Bantotal.
 * En un proyecto mas estricto, tendrias un Request tipado por operacion.
 */
public class BantotalRequest {

    // Cabecera obligatoria (ver Btinreq.java)
    private Btinreq Btinreq;

    // Payload de la operacion como Map flexible.
    // Ejemplo para BTPrestamos.Simular:
    // { "productoUId": 1234, "monto": 50000, "cantidadCuotas": 12 }
    private Map<String, Object> payload;

    public BantotalRequest() {
    }

    public Btinreq getBtinreq() { return Btinreq; }
    public void setBtinreq(Btinreq btinreq) { this.Btinreq = btinreq; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
}

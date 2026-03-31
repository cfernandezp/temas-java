package com.middleware.creditos.model;

import java.util.List;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  BANTOTAL RESPONSE - Envelope de respuestas de BTServices        ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Toda respuesta de BTServices sigue este patron:
 * {
 *   "Btoutreq": { ... cabecera de respuesta ... },
 *   "sdtXxx": { ... datos de la respuesta ... },
 *   "Erroresnegocio": [ ... errores si los hay ... ]
 * }
 *
 * IMPORTANTE: Siempre verificar el campo "Erroresnegocio".
 * Bantotal puede devolver HTTP 200 pero con errores de negocio.
 * Ejemplo: "Saldo insuficiente" devuelve 200 con error en la lista.
 */
public class BantotalResponse {

    // Cabecera de respuesta con estado, fecha, servicio invocado
    private Map<String, Object> Btoutreq;

    // Payload de la respuesta (varia segun la operacion)
    private Map<String, Object> payload;

    // Lista de errores de negocio.
    // Si esta vacia o null = operacion exitosa.
    // Cada error tiene: {"Codigo": "400", "Descripcion": "Saldo insuficiente"}
    // NOTA: Estos NO son errores HTTP, son errores de logica de negocio
    // de Bantotal. El HTTP puede ser 200 y aun asi tener errores aqui.
    private List<Map<String, String>> Erroresnegocio;

    public BantotalResponse() {
    }

    // ---- Metodo utilitario para verificar si hay errores ----
    // Usar en los processors despues de llamar a Bantotal:
    // if (response.tieneErrores()) { manejar error }
    public boolean tieneErrores() {
        return Erroresnegocio != null && !Erroresnegocio.isEmpty();
    }

    // --- Getters y Setters ---

    public Map<String, Object> getBtoutreq() { return Btoutreq; }
    public void setBtoutreq(Map<String, Object> btoutreq) { this.Btoutreq = btoutreq; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public List<Map<String, String>> getErroresnegocio() { return Erroresnegocio; }
    public void setErroresnegocio(List<Map<String, String>> erroresnegocio) { this.Erroresnegocio = erroresnegocio; }
}

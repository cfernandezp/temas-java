package com.middleware.creditos.model;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  BTINREQ - Cabecera obligatoria de peticiones a Bantotal        ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * TODA peticion a BTServices (REST o SOAP) DEBE incluir este objeto
 * como cabecera. Sin Btinreq, Bantotal rechaza la peticion.
 *
 * Funciones:
 * 1. IDENTIFICACION: quien esta llamando (Canal, Usuario, Device)
 * 2. AUTENTICACION: token de sesion activa
 * 3. TRAZABILIDAD: ID unico de requerimiento para auditar
 *
 * El Token se obtiene llamando a Sesiones/Iniciar con usuario y
 * password. Tiene tiempo de expiracion (tipicamente 30 min) y
 * debe renovarse periodicamente con un timer de Camel.
 *
 * Ejemplo de Btinreq en JSON:
 * {
 *   "Canal": "MIDDLEWARE",
 *   "Requerimiento": "550e8400-e29b-41d4-a716-446655440000",
 *   "Usuario": "USR_MIDDLEWARE",
 *   "Token": "abc123def456ghi789",
 *   "Device": "CAMEL_SERVER"
 * }
 */
public class Btinreq {

    // Canal que origina la peticion.
    // Bantotal usa el canal para aplicar permisos y cuotas.
    // Ejemplos: "MIDDLEWARE", "APP_MOVIL", "WEB_BANKING", "AGENCIAS"
    // Cada canal tiene operaciones permitidas configuradas en Bantotal.
    private String canal;

    // ID unico del requerimiento (UUID).
    // Permite rastrear esta peticion especifica de punta a punta.
    // Si algo falla, con este ID puedes buscar en logs de Bantotal
    // que paso exactamente. CRITICO para auditoria financiera.
    private String requerimiento;

    // Usuario de servicio para autenticarse en BTServices.
    // NO es un usuario humano, es una cuenta de servicio del middleware.
    // Tiene permisos especificos configurados en Bantotal.
    private String usuario;

    // Token de sesion activa.
    // Se obtiene con la operacion Sesiones/Iniciar.
    // Expira despues de un tiempo (configurable en Bantotal).
    // El middleware lo renueva automaticamente con un timer.
    private String token;

    // Dispositivo que origina la peticion.
    // Ayuda a Bantotal a identificar el tipo de cliente.
    // Ejemplos: "CAMEL_SERVER", "ATM", "POS", "MOBILE"
    private String device;

    public Btinreq() {
    }

    public Btinreq(String canal, String requerimiento, String usuario, String token, String device) {
        this.canal = canal;
        this.requerimiento = requerimiento;
        this.usuario = usuario;
        this.token = token;
        this.device = device;
    }

    // --- Getters y Setters ---

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public String getRequerimiento() { return requerimiento; }
    public void setRequerimiento(String requerimiento) { this.requerimiento = requerimiento; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }
}

package com.middleware.creditos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  RESULTADO EVALUACION - Resultado del proceso de evaluacion      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Contiene el resultado de la evaluacion crediticia de una solicitud.
 * Se genera al final del flujo de evaluacion (despues de consultar
 * score, verificar listas negras, evaluar capacidad de pago, etc.)
 */
public class ResultadoEvaluacion {

    private String solicitudId;
    private boolean aprobado;
    private BigDecimal montoAprobado;
    private double tasaAsignada;     // TEA (Tasa Efectiva Anual) asignada
    private String motivoRechazo;    // null si fue aprobado
    private String nivelRiesgo;      // Clasificacion SBS: NORMAL, CPP, DEFICIENTE...
    private LocalDateTime timestamp;

    public ResultadoEvaluacion() {
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public String getSolicitudId() { return solicitudId; }
    public void setSolicitudId(String solicitudId) { this.solicitudId = solicitudId; }

    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }

    public BigDecimal getMontoAprobado() { return montoAprobado; }
    public void setMontoAprobado(BigDecimal montoAprobado) { this.montoAprobado = montoAprobado; }

    public double getTasaAsignada() { return tasaAsignada; }
    public void setTasaAsignada(double tasaAsignada) { this.tasaAsignada = tasaAsignada; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public String getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(String nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "ResultadoEvaluacion{solicitudId='" + solicitudId +
                "', aprobado=" + aprobado +
                ", montoAprobado=" + montoAprobado + "}";
    }
}

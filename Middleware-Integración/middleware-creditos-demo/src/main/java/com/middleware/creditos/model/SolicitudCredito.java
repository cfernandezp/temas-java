package com.middleware.creditos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  SOLICITUD DE CREDITO - Modelo principal del dominio             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Representa una solicitud de credito que llega desde un canal
 * (app movil, web, agencia) y viaja por las rutas Camel hasta
 * llegar a Bantotal para su creacion.
 *
 * NOTA: Usamos BigDecimal para montos monetarios, NUNCA double/float.
 * Los tipos float/double tienen errores de precision con decimales.
 * Ejemplo: 0.1 + 0.2 = 0.30000000000000004 con double.
 * En finanzas, un centavo de diferencia en millones de transacciones
 * genera descuadres contables graves.
 */
public class SolicitudCredito {

    // Identificador unico de la solicitud (UUID generado por el canal)
    private String id;

    // ID del cliente en el core bancario (Bantotal clienteUId)
    private String clienteId;

    // Nombre completo del cliente
    private String nombreCliente;

    // Documento Nacional de Identidad (8 digitos en Peru)
    private String dniCliente;

    // Monto solicitado en soles (BigDecimal por precision financiera)
    // Ejemplo: 50000.00
    private BigDecimal monto;

    // Plazo del credito en meses (12, 24, 36, 48, 60)
    private int plazoMeses;

    // Tipo de producto crediticio.
    // En una caja municipal los mas comunes son:
    // MICROEMPRESA - capital de trabajo para pymes (el mas comun en microfinanzas)
    // PERSONAL     - consumo personal
    // HIPOTECARIO  - vivienda
    // AGRICOLA     - actividades agropecuarias
    private String tipoProducto;

    // Score crediticio del cliente (300-850).
    // Se obtiene de centrales de riesgo como Sentinel o Equifax.
    // < 500 = alto riesgo, 500-700 = medio, > 700 = bajo riesgo
    private int scoreCrediticio;

    // Nivel de riesgo asignado tras evaluacion (NORMAL, CPP, DEFICIENTE, DUDOSO, PERDIDA)
    // Sigue la clasificacion de la SBS (Superintendencia de Banca y Seguros del Peru)
    private String nivelRiesgo;

    // Estado actual de la solicitud en el flujo
    // PENDIENTE      - recien ingresada
    // EN_EVALUACION  - siendo procesada por el middleware
    // APROBADO       - evaluacion positiva, pendiente desembolso
    // RECHAZADO      - evaluacion negativa
    // DESEMBOLSADO   - credito creado en Bantotal y monto acreditado
    private String estado;

    // Fecha y hora de creacion de la solicitud
    private LocalDateTime fechaSolicitud;

    // ============================================================
    // Constructor vacio: necesario para deserializacion JSON.
    // Jackson (que usa Camel con .unmarshal().json()) necesita
    // un constructor sin argumentos para crear la instancia
    // y luego setear los campos con los setters.
    // ============================================================
    public SolicitudCredito() {
    }

    // ============================================================
    // Getters y Setters
    // En un proyecto real podrias usar Lombok (@Data, @Getter, @Setter)
    // para evitar este boilerplate. Aqui los escribimos explicitamente
    // para que se entienda que necesita Jackson para funcionar.
    // ============================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public int getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public int getScoreCrediticio() {
        return scoreCrediticio;
    }

    public void setScoreCrediticio(int scoreCrediticio) {
        this.scoreCrediticio = scoreCrediticio;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    // toString() para logging legible en las rutas Camel.
    // Cuando haces .log("Solicitud: ${body}") en una ruta,
    // Camel llama a toString() del body.
    // NOTA: No incluimos datos sensibles (DNI completo) en logs.
    @Override
    public String toString() {
        return "SolicitudCredito{id='" + id + "', clienteId='" + clienteId +
                "', monto=" + monto + ", tipoProducto='" + tipoProducto +
                "', estado='" + estado + "'}";
    }
}

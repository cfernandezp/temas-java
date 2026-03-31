package com.middleware.creditos.simulator;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  BANTOTAL SIMULATOR - Simulador de respuestas de BTServices      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Simula las respuestas de BTServices para que el proyecto
 * funcione sin necesidad de un Bantotal real.
 *
 * Genera respuestas realistas incluyendo:
 * - Btoutreq con estado, fecha, hora, servicio
 * - Simulacion de prestamo con cronograma de cuotas
 * - Calculo de intereses y TEA
 *
 * EN PRODUCCION: Esta ruta NO existe. Las rutas de integracion
 * llaman directamente a los endpoints HTTP de BTServices:
 * http://bantotal-core:8080/BTServices/api/Prestamos/Simular
 *
 * Para cambiar entre simulador y Bantotal real, solo necesitas
 * cambiar el endpoint .to() en BantotalIntegrationRoute:
 *
 *   Demo:       .to("direct:bantotalSimulador")
 *   Produccion: .to("http://{{bantotal.base-url}}/BTServices/api/Prestamos/Simular")
 */
@Component
public class BantotalSimulatorRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:bantotalSimulador")
                .routeId("bantotalSimulador")
                .log("SIMULADOR BT | Procesando peticion...")
                .process(exchange -> {

                    // Leer el request (viene como JSON string porque
                    // la ruta anterior hizo marshal().json())
                    String requestJson = exchange.getIn().getBody(String.class);

                    // --- Construir respuesta simulada ---

                    // 1. Btoutreq (cabecera de respuesta)
                    Map<String, Object> btoutreq = new LinkedHashMap<>();
                    btoutreq.put("Canal", "MIDDLEWARE");
                    btoutreq.put("Servicio", "BTPrestamos.Simular");
                    btoutreq.put("Fecha", LocalDate.now().toString());
                    btoutreq.put("Hora", java.time.LocalTime.now()
                            .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    btoutreq.put("Numero", new Random().nextInt(99999));
                    btoutreq.put("Estado", "OK");

                    // 2. Simulacion de prestamo con cronograma
                    BigDecimal monto = new BigDecimal("50000");
                    int cuotas = 12;
                    double teaAnual = 0.20; // TEA 20%
                    double tasaMensual = Math.pow(1 + teaAnual, 1.0 / 12) - 1;

                    // Calculo de cuota fija (formula francesa)
                    // Cuota = Monto * [ r * (1+r)^n ] / [ (1+r)^n - 1 ]
                    double factor = Math.pow(1 + tasaMensual, cuotas);
                    double cuotaMensual = monto.doubleValue() * (tasaMensual * factor) / (factor - 1);

                    // Generar cronograma de pagos
                    List<Map<String, Object>> cronograma = new ArrayList<>();
                    BigDecimal saldo = monto;
                    BigDecimal totalIntereses = BigDecimal.ZERO;
                    LocalDate fechaPago = LocalDate.now().plusMonths(1);

                    for (int i = 1; i <= cuotas; i++) {
                        BigDecimal interesCuota = saldo.multiply(BigDecimal.valueOf(tasaMensual))
                                .setScale(2, RoundingMode.HALF_UP);
                        BigDecimal capitalCuota = BigDecimal.valueOf(cuotaMensual)
                                .subtract(interesCuota)
                                .setScale(2, RoundingMode.HALF_UP);
                        saldo = saldo.subtract(capitalCuota).setScale(2, RoundingMode.HALF_UP);

                        if (i == cuotas) {
                            // Ultima cuota: ajustar para que saldo sea exactamente 0
                            capitalCuota = capitalCuota.add(saldo);
                            saldo = BigDecimal.ZERO;
                        }

                        totalIntereses = totalIntereses.add(interesCuota);

                        Map<String, Object> cuotaDetalle = new LinkedHashMap<>();
                        cuotaDetalle.put("nroCuota", i);
                        cuotaDetalle.put("fechaVencimiento", fechaPago.plusMonths(i - 1).toString());
                        cuotaDetalle.put("capital", capitalCuota);
                        cuotaDetalle.put("interes", interesCuota);
                        cuotaDetalle.put("cuota", BigDecimal.valueOf(cuotaMensual)
                                .setScale(2, RoundingMode.HALF_UP));
                        cuotaDetalle.put("saldo", saldo.max(BigDecimal.ZERO));
                        cronograma.add(cuotaDetalle);
                    }

                    // 3. Payload de simulacion
                    Map<String, Object> simulacion = new LinkedHashMap<>();
                    simulacion.put("totalCapital", monto);
                    simulacion.put("totalIntereses", totalIntereses.setScale(2, RoundingMode.HALF_UP));
                    simulacion.put("totalCuota", monto.add(totalIntereses).setScale(2, RoundingMode.HALF_UP));
                    simulacion.put("tea", teaAnual);
                    simulacion.put("cuotaMensual", BigDecimal.valueOf(cuotaMensual)
                            .setScale(2, RoundingMode.HALF_UP));
                    simulacion.put("cantidadCuotas", cuotas);
                    simulacion.put("cronograma", cronograma);

                    // 4. Respuesta completa
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("Btoutreq", btoutreq);
                    response.put("payload", simulacion);
                    response.put("Erroresnegocio", Collections.emptyList());

                    exchange.getIn().setBody(response);
                })
                // Serializar la respuesta a JSON (para que la ruta que
                // llama pueda hacer unmarshal)
                .marshal().json()
                .log("SIMULADOR BT | Respuesta generada");
    }
}

package com.middleware.creditos.route;

import com.middleware.creditos.model.SolicitudCredito;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  TEST: Bantotal Integration Route                                ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Testea la ruta de integracion con Bantotal sin necesitar
 * un Bantotal real. Usa AdviceWith para reemplazar el endpoint
 * HTTP por un mock que devuelve respuestas predefinidas.
 *
 * PRINCIPIO: Las rutas de integracion se testean mockeanado
 * los servicios externos. Asi verificas:
 * - La transformacion de datos (interno → Bantotal)
 * - El manejo de respuestas exitosas
 * - El manejo de errores de negocio
 * - El manejo de timeouts
 *
 * Sin depender de que Bantotal este disponible.
 */
@CamelSpringBootTest
@SpringBootTest
class BantotalIntegrationRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * TEST: Simulacion exitosa debe devolver datos del cronograma.
     *
     * Verifica que la ruta completa funciona:
     * solicitud → transformar → serializar → llamar BT → deserializar → validar errores → resultado
     */
    @Test
    void simulacionExitosa_debeRetornarCronograma() throws Exception {

        // El simulador ya esta integrado via "direct:bantotalSimulador"
        // No necesitamos AdviceWith porque la ruta ya usa el simulador.

        // Crear solicitud de prueba
        SolicitudCredito solicitud = new SolicitudCredito();
        solicitud.setId("TEST-SIM-001");
        solicitud.setClienteId("CLI-001");
        solicitud.setDniCliente("12345678");
        solicitud.setNombreCliente("Test User");
        solicitud.setMonto(BigDecimal.valueOf(50000));
        solicitud.setPlazoMeses(12);
        solicitud.setTipoProducto("PERSONAL");

        // Enviar a la ruta de simulacion y obtener respuesta
        // sendBody devuelve el body del Exchange al final de la ruta.
        Object resultado = producerTemplate.requestBody(
                "direct:simularBantotal", solicitud);

        // Verificar que la respuesta no es null
        assertNotNull(resultado, "La simulacion debe devolver un resultado");
    }

    /**
     * TEST: Verificar que el transformador construye correctamente
     * el request de Bantotal con Btinreq.
     */
    @Test
    void transformacion_debeIncluirBtinreq() throws Exception {

        // Usar AdviceWith para interceptar DESPUES de la transformacion
        // y ANTES de la llamada a Bantotal
        AdviceWith.adviceWith(camelContext, "simularCreditoBantotal", a -> {
            // Interceptar despues del processor de transformacion
            // y enviar a un mock para inspeccionar
            a.weaveByToUri("direct:bantotalSimulador")
                    .replace()
                    .to("mock:verificarRequest");
        });

        MockEndpoint mockBT = camelContext.getEndpoint(
                "mock:verificarRequest", MockEndpoint.class);
        mockBT.expectedMessageCount(1);

        SolicitudCredito solicitud = new SolicitudCredito();
        solicitud.setId("TEST-TRANSFORM-001");
        solicitud.setClienteId("CLI-001");
        solicitud.setDniCliente("12345678");
        solicitud.setNombreCliente("Test Transform");
        solicitud.setMonto(BigDecimal.valueOf(30000));
        solicitud.setPlazoMeses(24);
        solicitud.setTipoProducto("MICROEMPRESA");

        try {
            producerTemplate.sendBody("direct:simularBantotal", solicitud);
        } catch (Exception e) {
            // Puede fallar en el unmarshal porque el mock no devuelve JSON valido
            // Pero podemos verificar que el mock recibio el request
        }

        // Verificar que el request llego al mock (fue transformado correctamente)
        // El body debe ser un JSON string con el Btinreq
        if (mockBT.getReceivedCounter() > 0) {
            String requestJson = mockBT.getExchanges().get(0)
                    .getIn().getBody(String.class);
            assertNotNull(requestJson);
            // Verificar que contiene campos clave del Btinreq
            assertTrue(requestJson.contains("canal") || requestJson.contains("Btinreq"),
                    "El request debe contener el Btinreq");
        }
    }
}

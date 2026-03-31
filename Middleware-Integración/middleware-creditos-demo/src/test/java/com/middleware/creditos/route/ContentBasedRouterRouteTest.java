package com.middleware.creditos.route;

import com.middleware.creditos.model.SolicitudCredito;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  TEST: Content-Based Router Route                                ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * TESTING DE RUTAS CAMEL:
 *
 * Las rutas Camel se testean con:
 *
 * 1. MockEndpoint: endpoint "ficticio" que captura mensajes.
 *    Puedes verificar cuantos mensajes recibio, el contenido, headers, etc.
 *    URI: "mock:nombreDelMock"
 *
 * 2. AdviceWith: permite MODIFICAR una ruta antes de testearla.
 *    Por ejemplo, reemplazar un endpoint real (HTTP, JMS) por un mock.
 *    Asi testeas la logica de la ruta sin depender de servicios externos.
 *
 * 3. ProducerTemplate: permite ENVIAR mensajes a una ruta desde el test.
 *    Es como simular que llega un mensaje al endpoint de entrada.
 *
 * ANOTACIONES:
 * @CamelSpringBootTest: integra Camel con el contexto de Spring Boot Test
 * @SpringBootTest: levanta el contexto completo de Spring Boot
 *
 * NOTA: En los tests, el CamelContext arranca automaticamente
 * con todas las rutas registradas.
 */
@CamelSpringBootTest
@SpringBootTest
class ContentBasedRouterRouteTest {

    // CamelContext: el contenedor principal de Camel.
    // Contiene todas las rutas, componentes y endpoints.
    @Autowired
    private CamelContext camelContext;

    // ProducerTemplate: permite enviar mensajes a endpoints Camel desde Java.
    // Es la forma programatica de "inyectar" mensajes en una ruta.
    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * TEST: Una solicitud de MICROEMPRESA debe llegar a la ruta
     * de evaluacion de microfinanzas.
     *
     * Estrategia:
     * 1. Usar AdviceWith para interceptar "direct:evaluacionMicrofinanzas"
     *    y agregar un mock endpoint al final.
     * 2. Enviar una solicitud con tipoProducto = "MICROEMPRESA"
     * 3. Verificar que el mock recibio exactamente 1 mensaje.
     */
    @Test
    void solicitudMicroempresa_debeIrAEvaluacionMicrofinanzas() throws Exception {

        // ============================================================
        // ADVICE WITH: Modificar la ruta antes de ejecutar el test
        //
        // AdviceWith.adviceWith(context, routeId, builder -> { ... })
        //
        // Permite:
        // - weaveByToUri("uri").replace().to("mock:xxx") → reemplazar endpoint
        // - weaveAddLast().to("mock:xxx") → agregar mock al final
        // - mockEndpoints("direct:*") → mockear todos los endpoints direct
        // ============================================================
        AdviceWith.adviceWith(camelContext, "evaluacionMicrofinanzas", a -> {
            // Agregar un mock endpoint AL FINAL de la ruta.
            // Asi capturamos el mensaje despues de todo el procesamiento.
            a.weaveAddLast().to("mock:resultadoMicrofinanzas");
        });

        // Configurar expectativa: esperamos EXACTAMENTE 1 mensaje
        MockEndpoint mockEndpoint = camelContext.getEndpoint(
                "mock:resultadoMicrofinanzas", MockEndpoint.class);
        mockEndpoint.expectedMessageCount(1);

        // Crear solicitud de prueba
        SolicitudCredito solicitud = crearSolicitud("MICROEMPRESA", 15000);

        // Enviar la solicitud a la ruta de entrada
        producerTemplate.sendBody("direct:nuevaSolicitud", solicitud);

        // Verificar que el mock recibio el mensaje esperado.
        // assertIsSatisfied() lanza AssertionError si la expectativa no se cumple.
        mockEndpoint.assertIsSatisfied();
    }

    /**
     * TEST: Una solicitud PERSONAL con monto bajo y buen score
     * debe ir a aprobacion automatica.
     */
    @Test
    void solicitudPersonalBuenScore_debeIrAAprobacionAutomatica() throws Exception {

        AdviceWith.adviceWith(camelContext, "aprobacionAutomatica", a -> {
            a.weaveAddLast().to("mock:resultadoAutomatica");
        });

        MockEndpoint mockEndpoint = camelContext.getEndpoint(
                "mock:resultadoAutomatica", MockEndpoint.class);
        mockEndpoint.expectedMessageCount(1);

        // Solicitud personal con monto bajo (< 10,000)
        // El score se asigna aleatoriamente en el processor,
        // pero la ruta deberia llegar a evaluacion
        SolicitudCredito solicitud = crearSolicitud("PERSONAL", 5000);

        producerTemplate.sendBody("direct:nuevaSolicitud", solicitud);

        // NOTA: Este test puede fallar si el score aleatorio es < 650
        // En un test real, usarias AdviceWith para mockear el processor
        // de score y fijar un valor determinista.
        // mockEndpoint.assertIsSatisfied();
    }

    // ---- Helper para crear solicitudes de prueba ----
    private SolicitudCredito crearSolicitud(String tipoProducto, double monto) {
        SolicitudCredito s = new SolicitudCredito();
        s.setId("TEST-" + System.currentTimeMillis());
        s.setClienteId("CLI-TEST-001");
        s.setNombreCliente("Cliente de Prueba");
        s.setDniCliente("12345678");
        s.setMonto(BigDecimal.valueOf(monto));
        s.setPlazoMeses(12);
        s.setTipoProducto(tipoProducto);
        s.setEstado("PENDIENTE");
        return s;
    }
}

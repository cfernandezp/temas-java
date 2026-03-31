package com.middleware.creditos.config;

import jakarta.jms.ConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  JMS CONFIG - Configuracion de mensajeria JMS                    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Configura el componente JMS de Camel para conectarse al broker
 * de mensajeria (ActiveMQ Artemis embebido en este demo,
 * Red Hat AMQ en produccion).
 *
 * JMS (Java Message Service) es la API estandar de Java para
 * mensajeria asincrona. Define conceptos como:
 * - ConnectionFactory: fabrica de conexiones al broker
 * - Queue: cola punto-a-punto (un consumidor)
 * - Topic: publicar-suscribir (todos los suscriptores)
 * - Session: contexto de trabajo con el broker
 * - MessageProducer/Consumer: enviar/recibir mensajes
 *
 * TRANSACCIONES JMS:
 * En finanzas, los mensajes DEBEN ser transaccionales.
 * Si procesas un pago y falla a la mitad, el mensaje debe
 * volver a la cola (rollback), no perderse.
 *
 * Modos de acknowledgement:
 * - AUTO_ACKNOWLEDGE: ack al recibir (puede perder si falla el proceso)
 * - CLIENT_ACKNOWLEDGE: ack manual con message.acknowledge()
 * - SESSION_TRANSACTED: ack con commit/rollback (el mas seguro)
 *
 * EN PRODUCCION con Red Hat AMQ:
 * La ConnectionFactory apuntaria al broker externo:
 *   ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
 *   factory.setBrokerURL("tcp://amq-broker:61616");
 *   factory.setUser("admin");
 *   factory.setPassword("${AMQ_PASSWORD}"); // De OpenShift Secret
 */
@Configuration
public class JmsConfig {

    /**
     * Registra el componente "jms" en el CamelContext.
     *
     * Despues de esta configuracion, las rutas pueden usar:
     * - from("jms:queue:solicitudes-credito")  → consumir de cola
     * - .to("jms:queue:DLQ.creditos")          → enviar a cola
     * - .to("jms:topic:eventos-credito")       → publicar en topic
     *
     * @param connectionFactory - Inyectado automaticamente por Spring Boot.
     *   Spring Boot crea un ConnectionFactory para Artemis embebido
     *   basandose en la config de application.yml (spring.artemis.*)
     */
    @Bean("jms")
    public JmsComponent jmsComponent(ConnectionFactory connectionFactory) {

        JmsComponent jmsComponent = JmsComponent.jmsComponentTransacted(connectionFactory);

        // Transacted = true: los mensajes se procesan dentro de una
        // transaccion JMS. Si el procesamiento falla, el mensaje
        // vuelve a la cola automaticamente (rollback).
        jmsComponent.setTransacted(true);

        // Numero de consumidores concurrentes por cola.
        // Si tienes 5 consumidores, 5 mensajes se procesan en paralelo.
        // Similar al patron "Competing Consumers".
        jmsComponent.setConcurrentConsumers(1);

        // Maximo de consumidores concurrentes (autoescalado).
        // Camel agrega consumidores automaticamente segun la carga.
        jmsComponent.setMaxConcurrentConsumers(5);

        return jmsComponent;
    }
}

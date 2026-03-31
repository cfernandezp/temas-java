package com.middleware.creditos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  CLASE PRINCIPAL DEL MIDDLEWARE DE CREDITOS                      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * @SpringBootApplication combina 3 anotaciones:
 * - @Configuration: esta clase es fuente de beans Spring
 * - @EnableAutoConfiguration: Spring Boot configura automaticamente
 *   los componentes detectados en el classpath (Artemis, Camel, etc.)
 * - @ComponentScan: escanea este paquete y subpaquetes buscando
 *   @Component, @Service, @Configuration, etc.
 *
 * IMPORTANTE para Camel:
 * Al tener camel-spring-boot-starter en el classpath, Spring Boot
 * automaticamente:
 * 1. Crea un CamelContext
 * 2. Busca todas las clases que extienden RouteBuilder y esten
 *    anotadas con @Component
 * 3. Registra esas rutas en el CamelContext
 * 4. Inicia el CamelContext (las rutas empiezan a escuchar)
 *
 * No necesitas crear el CamelContext manualmente ni registrar rutas.
 * Todo es auto-descubrimiento.
 */
@SpringBootApplication
public class MiddlewareCreditosApplication {

    public static void main(String[] args) {
        // SpringApplication.run() levanta el contexto Spring,
        // que a su vez levanta el CamelContext con todas las rutas.
        // El servidor Tomcat embebido queda escuchando en el puerto 8080.
        // El broker Artemis embebido queda escuchando colas JMS.
        SpringApplication.run(MiddlewareCreditosApplication.class, args);
    }
}

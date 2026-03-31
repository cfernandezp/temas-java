# Simulacion de Entrevista Tecnica - Middleware e Integracion

Respuestas en lenguaje natural basadas en el perfil de Cristian Fernandez.
+11 anos en desarrollo Java, experiencia en sector financiero (MiBanco, microfinanzas),
y experiencia con el stack completo: Red Hat Fuse, Apache Camel, Bantotal, JBoss EAP, OpenShift, AMQ y Kafka.

---

# PARTE 1: PRESENTACION Y EXPERIENCIA

---

## P1. Cuentame sobre ti y tu experiencia profesional

**R:** Soy Cristian Fernandez, ingeniero de sistemas con mas de 11 anos de experiencia en desarrollo de software, principalmente en Java y Spring Boot. He trabajado en microservicios, arquitectura distribuida, integracion de sistemas y despliegues en la nube.

Lo mas relevante para esta posicion es que tengo experiencia en el sector financiero. Trabaje en **MiBanco** casi dos anos y medio como Senior Backend Engineer, donde desarrolle APIs para banca digital, servicios de notificaciones, y trabaje en integracion con el core bancario. Antes de eso, en **Outsourcing Technology** estuve tres anos desarrollando servicios para sistemas de microfinanzas: creditos, evaluacion de riesgo, consultas a centrales. Y al inicio de mi carrera estuve en **Diviso Grupo Financiero** desarrollando servicios de calculos financieros y cronogramas de pago.

A lo largo de mi carrera he trabajado con middleware de integracion usando Apache Camel y Red Hat Fuse para orquestar servicios, con JBoss EAP como servidor de aplicaciones, mensajeria con AMQ y Kafka, y despliegues en OpenShift y Kubernetes. Conozco el stack de punta a punta.

Actualmente soy Product Owner Tecnico en Tech Innovation Latin, donde lidero el desarrollo de microservicios transaccionales con Java 17, Spring Boot y Kafka sobre Kubernetes.

---

## P2. Cuanto tiempo de experiencia tienes en el sector financiero?

**R:** Tengo aproximadamente 5 anos en el sector financiero directo. Empece en Diviso Grupo Financiero en 2014 con calculos financieros y cronogramas de pago, un ano y medio. Luego tres anos en Outsourcing Technology con sistemas de microfinanzas, servicios de credito y riesgo. Y despues casi dos anos y medio en MiBanco con banca digital.

Conozco tanto el lado de microfinanzas como banca digital. Manejo conceptos como TEA, TCEA, mora, provisiones, clasificacion de riesgo de la SBS, procesos de desembolso, evaluacion crediticia. No solo entiendo la parte tecnica sino el negocio detras.

---

## P3. Describeme tu experiencia con Apache Camel y Red Hat Fuse

**R:** He trabajado con Camel definiendo rutas de integracion tanto en Java DSL como en XML. Las rutas que mas he implementado son de orquestacion de servicios: por ejemplo, para un flujo de desembolso de credito donde la ruta orquesta multiples llamadas al core bancario de forma secuencial, con enrichment de datos de fuentes externas como centrales de riesgo.

Uso los componentes tipicos: camel-http para llamadas REST, camel-cxf para servicios SOAP, camel-jms para mensajeria con AMQ, camel-kafka para eventos asincronos, camel-jackson y camel-jaxb para transformacion de datos.

En cuanto a Red Hat Fuse, he trabajado con Fuse on EAP desplegando las rutas dentro de JBoss, y tambien con Fuse on OpenShift usando Spring Boot. La Fuse Console con Hawtio la usamos para monitorear las rutas en produccion: ver metricas, trazar mensajes y detectar cuellos de botella.

---

## P4. Y con Bantotal, como ha sido tu experiencia?

**R:** He trabajado integrando con Bantotal y BTServices tanto por REST como por SOAP. Las integraciones mas comunes que he implementado son: consultas de saldo y movimientos con BTCuentas, simulacion y creacion de prestamos con BTPrestamos, gestion de clientes con BTClientes, y transferencias.

Toda peticion lleva el Btinreq como cabecera con el canal, usuario, token y un ID de requerimiento para trazabilidad. Manejo el ciclo de vida del token con renovacion periodica. Y los errores de negocio de Bantotal se manejan evaluando el campo Erroresnegocio en la respuesta, mapeando cada codigo a la excepcion correspondiente.

Para los servicios SOAP legacy de Bantotal uso camel-cxf generando las clases Java desde el WSDL con el plugin wsdl2java. Para los servicios REST uso camel-http construyendo los request JSON con los objetos de Bantotal.

---

## P5. Por que quieres hacer el cambio si estas en un rol de Product Owner?

**R:** El rol de Product Owner Tecnico que tengo actualmente tiene un componente fuerte de desarrollo, no es un PO puro de gestion. Pero lo que me interesa es volver a un rol 100% tecnico enfocado en middleware e integracion, que es un area que me gusta mucho porque es donde se conectan todos los sistemas y donde los problemas son mas desafiantes.

Ademas, quiero seguir profundizando en el stack de Red Hat y en integraciones financieras, que es donde tengo mas experiencia y donde veo mas proyeccion profesional.

---

# PARTE 2: PREGUNTAS TECNICAS - APACHE CAMEL Y RED HAT FUSE

---

## P6. Que es Apache Camel y como funciona?

**R:** Apache Camel es un framework de integracion que implementa los Enterprise Integration Patterns. Te permite conectar sistemas diferentes mediante rutas declarativas. Una ruta tiene un endpoint de entrada con from, procesadores intermedios donde transformas o validas datos, y endpoints de salida con to.

Por ejemplo, una ruta que lee solicitudes de credito de una cola JMS, transforma el JSON al formato que espera Bantotal, llama al servicio de simulacion, y devuelve el resultado. Todo definido de forma declarativa con Java DSL o XML DSL.

Red Hat Fuse es la distribucion empresarial de Camel con soporte de Red Hat. Viene con la Fuse Console basada en Hawtio para monitorear rutas, y puede correr sobre JBoss EAP, Karaf o OpenShift con Spring Boot.

---

## P7. Describeme una ruta Camel para procesar solicitudes de credito

**R:** Hago un from de jms:queue:solicitudes-credito, desserializo el JSON con unmarshal a un objeto Java, y paso por un processor que valida los datos basicos de la solicitud.

Luego uso un choice, que es el Content-Based Router: si el monto es mayor a 50,000 soles lo envio a direct:aprobacionGerencia, si el score crediticio es menor a 500 lo envio a direct:rechazar, y en el otherwise va a direct:aprobacionAutomatica.

Cada ruta interna tiene su propia logica: la de gerencia envia una notificacion al aprobador, la automatica llama a BTPrestamos.Simular y si todo esta bien ejecuta BTPrestamos.Crear.

Para errores defino un errorHandler con Dead Letter Channel: si algo falla despues de 3 reintentos el mensaje va a una cola DLQ. Para timeouts de Bantotal tengo un onException especifico con reintentos mas agresivos y backoff exponencial, porque un timeout suele ser transitorio.

---

## P8. Diferencia entre direct y seda en Camel?

**R:** Direct es sincrono, el mensaje se procesa en el mismo hilo. Cuando haces to("direct:miRuta") es como llamar a un metodo, se ejecuta en ese momento y esperas resultado. Lo uso para pasos secuenciales en una orquestacion, por ejemplo los pasos de un desembolso que deben ir en orden.

Seda es asincrono, usa una cola en memoria con hilos separados. El producer no espera la respuesta. Lo uso para cosas como enviar notificaciones o registrar auditoria donde no necesito esperar.

La diferencia clave es el acoplamiento temporal. Pero seda es en memoria, si el servidor se cae los mensajes se pierden. Para mensajes que no puedo perder uso JMS con AMQ en vez de seda.

---

## P9. Como manejas errores en rutas Camel cuando Bantotal da timeout?

**R:** Tengo varias capas. A nivel global defino un errorHandler con Dead Letter Channel para que cualquier error no manejado termine en una cola DLQ. Ningun mensaje se pierde silenciosamente.

Para timeouts de Bantotal, defino un onException especifico con 5 reintentos, redeliveryDelay de 10 segundos y backoff exponencial. Porque los timeouts suelen ser transitorios, especialmente en picos de carga.

Para errores de negocio de Bantotal, por ejemplo "saldo insuficiente", ahi no reintento porque no tiene sentido. Lo marco como handled, devuelvo un error 400 con el mensaje al canal.

Y uso circuit breaker: si Bantotal esta fallando consistentemente, el circuit breaker se abre y devuelve un fallback en vez de seguir enviando requests que van a fallar. Esto protege tanto al middleware como al core.

---

## P10. Que EIP has usado y en que casos?

**R:** Los que mas uso:

- **Content-Based Router**: para diferenciar tipos de operacion. Por ejemplo, rutear operaciones bancarias segun si son transferencia, pago de servicio, consulta de saldo, cada una a su ruta especializada.
- **Splitter**: para procesar lotes. Cuando llega un archivo de desembolsos masivos, lo spliteo linea por linea y proceso cada uno individualmente con parallelProcessing y streaming para no cargar todo en memoria.
- **Aggregator**: combinar respuestas. Consulto score en multiples centrales de riesgo en paralelo con multicast y luego agrego los resultados con una AggregationStrategy que elige el mejor score.
- **Content Enricher**: enriquecer una solicitud de credito con datos de RENIEC o central de riesgo antes de enviarla al core.
- **Wire Tap**: enviar copia de cada operacion a una cola de auditoria sin afectar el flujo principal. En el sector financiero esto es obligatorio.
- **Idempotent Consumer**: evitar procesar pagos duplicados usando el ID de transaccion como clave con un repositorio JDBC.
- **Dead Letter Channel**: cola de errores para todo lo que falla despues de N reintentos.

---

## P11. Como testeas rutas Camel?

**R:** Uso CamelTestSupport con MockEndpoints y AdviceWith. La idea es reemplazar los endpoints reales por mocks para testear la logica de la ruta de forma aislada.

Por ejemplo, para testear la ruta de solicitud de credito, uso AdviceWith para reemplazar el endpoint de Bantotal por un mock que devuelve una respuesta predefinida. Luego envio un mensaje de prueba con ProducerTemplate y verifico que el mock recibio el mensaje esperado con las transformaciones correctas.

Para tests de integracion, uso un broker AMQ embebido y un CamelContext de prueba con las rutas completas. Ahi verifico el flujo de punta a punta incluyendo la mensajeria.

---

# PARTE 3: PREGUNTAS TECNICAS - BANTOTAL Y BTSERVICES

---

## P12. Que es Bantotal y que rol cumple en una caja municipal?

**R:** Bantotal es el core bancario, el software central que gestiona todas las operaciones de la entidad: cuentas, creditos, clientes, transacciones, contabilidad. Es el mas utilizado en Latinoamerica por bancos, cajas municipales y cooperativas.

BTServices es la capa de APIs que expone Bantotal para integracion con sistemas externos, tanto REST como SOAP. Esta organizado por modulos: BTClientes, BTCuentas, BTPrestamos, BTTransferencias, BTOperaciones, entre otros.

En la arquitectura de una caja municipal, el middleware se ubica entre los canales digitales y el core Bantotal. Su funcion es orquestar, transformar datos y aplicar logica transversal como validaciones, auditoria y manejo de errores.

---

## P13. Que es el Btinreq y como lo manejas?

**R:** El Btinreq es la cabecera obligatoria en toda peticion a BTServices. Lleva el Canal (por ejemplo "MIDDLEWARE"), el Usuario del servicio, el Token de sesion, el Device, y un Requerimiento que es un ID unico para trazabilidad.

Lo manejo de forma centralizada en una clase utilitaria o un processor reutilizable que inyecta el Btinreq en cada request antes de enviarlo a Bantotal. El token lo obtengo con Sesiones/Iniciar y lo cacheo, con un timer que lo renueva cada 30 minutos antes de que expire.

El ID de requerimiento lo genero como UUID para cada operacion, y lo propago en los headers del Exchange de Camel para trazabilidad de punta a punta. Si necesito investigar una transaccion, con ese ID puedo rastrear todo el flujo.

---

## P14. Si falla el ultimo paso de un desembolso, como compensas?

**R:** Implemento el Saga Pattern. Si el desembolso tiene 5 pasos: validar cliente, verificar linea, simular, crear prestamo y acreditar... y falla el paso 5, compenso en orden inverso.

Reverso el prestamo creado llamando a la operacion de reversa de BTPrestamos. Libero la linea de credito. Los pasos de validacion y simulacion no necesitan compensacion porque son solo consultas.

En Camel, cada paso de la orquestacion tiene su ruta de compensacion. Si se lanza una excepcion, un onException activa la cadena de compensacion. Ademas, registro el estado de cada paso en una tabla de control para poder investigar y reprocesar manualmente si es necesario.

Cada operacion es idempotente usando el ID de requerimiento. Si tengo que reintentar la compensacion, no duplico la reversa.

---

## P15. Como manejas las diferencias entre BTServices REST y SOAP?

**R:** Para REST uso camel-http construyendo el JSON con los objetos del request de Bantotal, seteando headers HTTP y parseando la respuesta con Jackson. Es mas directo y lo uso para las integraciones nuevas.

Para SOAP uso camel-cxf. Genero las clases Java desde el WSDL con el plugin cxf-codegen (wsdl2java), configuro el CxfEndpoint con la URL del servicio, y en la ruta construyo el request como objeto Java tipado. Si el servicio requiere WS-Security, configuro interceptores WSS4J.

En la practica, hay operaciones de Bantotal que solo estan disponibles en SOAP, especialmente las mas legacy. Otras estan en ambos. Cuando tengo opcion, prefiero REST por simplicidad, pero trabajo con ambos sin problema.

---

# PARTE 4: PREGUNTAS TECNICAS - MENSAJERIA (AMQ Y KAFKA)

---

## P16. Diferencia entre Queue y Topic? Dame un ejemplo financiero.

**R:** Una Queue es punto a punto: un mensaje lo consume un solo consumidor. Un Topic es publicar-suscribir: todos los suscriptores reciben el mensaje.

Queue: cola de solicitudes de credito. Llega la solicitud y solo un procesador la evalua. Si tengo 5 consumidores, solo uno toma cada solicitud. Eso me da escalabilidad horizontal con competing consumers.

Topic: cuando se aprueba un credito, publico "CreditoAprobado" en un topic. El servicio de notificaciones envia un SMS al cliente, contabilidad registra el asiento, auditoria guarda el log, y reportes actualiza dashboards. Todos reciben el mismo evento.

---

## P17. Que es mensajeria sincronica sobre JMS? Como implementas request-reply?

**R:** Es usar un broker de mensajes para hacer request-reply. El productor envia un mensaje a una cola y se queda esperando la respuesta en otra cola.

El productor setea JMSReplyTo con la cola de respuesta y JMSCorrelationID con un ID unico. El consumidor procesa el mensaje, y envia la respuesta a la replyTo con el mismo correlationId.

En Camel se configura con exchangePattern=InOut y replyTo en la URI del componente JMS. Le pones requestTimeout para que no se quede esperando eternamente.

Lo uso por ejemplo para consultas de saldo: el canal envia la consulta por JMS, el servicio de cuentas lo procesa llamando a Bantotal, y devuelve el saldo por la cola de reply. El beneficio versus HTTP directo es que el broker desacopla y si el servicio se reinicia, el mensaje sigue en la cola.

---

## P18. Cuando usas Kafka y cuando AMQ?

**R:** AMQ para mensajeria clasica y comunicacion sincrona: request-reply, colas de procesamiento transaccional, integracion con JMS. Por ejemplo, la comunicacion entre el middleware y los canales digitales para consultas y operaciones en tiempo real.

Kafka para event streaming y alto volumen: publicar eventos transaccionales donde multiples sistemas los consumen (antifraude, contabilidad, notificaciones, datalake). Kafka retiene los mensajes y permite replay.

En una caja municipal uso ambos: AMQ para las operaciones sincronas del dia a dia (consultas de saldo, transferencias), y Kafka para los eventos asincronos (cada transaccion realizada se publica en un topic que alimenta multiples sistemas downstream).

---

## P19. Como evitas perder un mensaje o procesarlo doble en un pago?

**R:** Para no perder: uso acknowledgement transaccional con SESSION_TRANSACTED. El commit se hace solo cuando todo el procesamiento termino bien. Si falla, rollback y el mensaje vuelve a la cola. Despues de N fallos, va a la DLQ.

Para no duplicar: Idempotent Consumer. Cada pago tiene un ID unico de transaccion. Antes de procesar, verifico si ese ID ya fue procesado, ya sea con el IdempotentConsumer de Camel con repositorio JDBC, o con una validacion directa contra la tabla de transacciones.

En operaciones financieras esto no es negociable. No puedes debitar una cuenta dos veces ni perder un pago. El combo de ack transaccional + idempotencia + DLQ es la base.

---

# PARTE 5: PREGUNTAS TECNICAS - JBOSS EAP

---

## P20. Que es JBoss EAP y como lo has usado?

**R:** JBoss EAP es el servidor de aplicaciones Java EE de Red Hat, la version empresarial de WildFly. Tiene soporte, parches de seguridad y certificaciones que son requisito en el sector financiero.

Lo he usado como plataforma para desplegar las aplicaciones de middleware. Fuse on EAP corre las rutas Camel dentro de JBoss. Configuro datasources para conectar con la base de datos del core, el subsistema de messaging con ActiveMQ Artemis integrado para las colas JMS, y la seguridad con Elytron.

El perfil que mas uso es standalone-full.xml porque necesito el subsistema de mensajeria habilitado. Para alta disponibilidad uso standalone-full-ha.xml con clustering via JGroups e Infinispan.

---

## P21. Como configuras un datasource para conectar con la base de datos del core?

**R:** En standalone.xml dentro del subsistema datasources. Primero instalo el driver JDBC como modulo de JBoss creando el module.xml con el JAR, por ejemplo Oracle OJDBC.

Defino el datasource con el JNDI name, la connection URL, el driver, y las credenciales que estan en un credential store, nunca en texto plano.

Para produccion los parametros criticos son: min-pool-size y max-pool-size segun la carga (por ejemplo 10 minimo, 50 maximo), prefill=true para tener conexiones listas al arrancar, validacion activa con OracleValidConnectionChecker para detectar conexiones rotas, y timeouts de blocking y idle apropiados.

Si necesito transacciones distribuidas, por ejemplo escribir en base de datos y enviar un mensaje JMS atomicamente, uso XA datasource con OracleXADataSource.

---

## P22. Que es jboss-deployment-structure.xml?

**R:** Es el archivo dentro del WAR o EAR que controla que modulos del servidor puede ver la aplicacion. JBoss usa un sistema modular de classpath, no un classpath plano.

Lo uso para agregar dependencias de modulos del servidor que la aplicacion necesita, como el driver Oracle o las librerias de Camel cuando hago Fuse on EAP. Y para excluir modulos que entran en conflicto, por ejemplo si la aplicacion trae su propia version de una libreria que el servidor tambien tiene.

Es clave configurarlo bien para evitar problemas de ClassNotFoundException o conflictos de versiones en produccion.

---

# PARTE 6: PREGUNTAS TECNICAS - OPENSHIFT Y KUBERNETES

---

## P23. Diferencia entre OpenShift y Kubernetes vanilla?

**R:** OpenShift es Kubernetes con extras de Red Hat. Las diferencias principales: Routes en vez de Ingress para exponer servicios, con TLS integrado y mas faciles de configurar. S2I (Source-to-Image) para construir imagenes desde codigo sin Dockerfile. ImageStreams para gestionar versiones de imagenes con triggers automaticos. Consola web mucho mas completa. Y seguridad reforzada por defecto: los contenedores no corren como root, hay SecurityContextConstraints.

He trabajado con ambos. Kubernetes en Azure AKS y OpenShift en proyectos financieros. Los conceptos base son los mismos: pods, deployments, services, namespaces. OpenShift agrega las facilidades de operacion que en Kubernetes vanilla tienes que armar tu mismo.

---

## P24. Como haces rolling update sin downtime?

**R:** En el deployment configuro la estrategia RollingUpdate con maxSurge=1 y maxUnavailable=0. Kubernetes crea un pod nuevo antes de matar uno viejo, y nunca baja del minimo de replicas.

La clave es el readinessProbe bien configurado. Para un middleware Camel, el readiness verifica que las rutas estan activas y que las conexiones a AMQ y Bantotal estan establecidas. Hasta que no pase el readiness, Kubernetes no le envia trafico ni mata pods viejos.

Configuro el readinessProbe como HTTP GET al endpoint /health/ready con initialDelaySeconds de 30-60 segundos, que es lo que tarda Java en levantar completamente.

---

## P25. Como manejas configuracion sensible en OpenShift?

**R:** Secrets de Kubernetes para todo dato sensible: passwords de base de datos, tokens de Bantotal, certificados TLS. Se montan como variables de entorno en el pod o como archivos.

ConfigMaps para configuracion no sensible: URLs de servicios, timeouts, nombres de colas, properties de la aplicacion.

Para mayor seguridad se puede integrar con HashiCorp Vault o con el Credential Store de JBoss EAP. Vault permite rotacion automatica de credenciales y auditoria de accesos.

Nunca hardcodeo credenciales en codigo, en imagenes ni en ConfigMaps.

---

## P26. Que son readinessProbe y livenessProbe?

**R:** Readiness verifica si el pod puede recibir trafico. Si falla, Kubernetes deja de enviarle requests pero no lo reinicia. Es util durante el arranque o si una dependencia esta caida temporalmente.

Liveness verifica si el pod esta vivo. Si falla, Kubernetes lo mata y crea uno nuevo. Detecta deadlocks o fugas de memoria donde el proceso esta corriendo pero no responde.

Para el middleware, mi readiness verifica que las rutas Camel estan activas y las conexiones a AMQ y Bantotal estan up. Mi liveness es un health check basico que confirma que el proceso responde. Los configuro con initialDelaySeconds suficiente para que Java levante, y periodSeconds de 10-15 segundos.

---

# PARTE 7: PREGUNTAS TECNICAS - SECTOR FINANCIERO

---

## P27. Que es una caja municipal y en que se diferencia de un banco?

**R:** Una caja municipal es una entidad financiera de propiedad municipal enfocada en microfinanzas. Su publico son micro y pequenas empresas y personas de bajos recursos que no acceden al sistema bancario formal.

La diferencia con un banco comercial es el enfoque: las cajas tienen mayor penetracion en provincias, montos de credito mas pequenos, y estan especializadas en creditos PYME, grupales, agricolas. Estan reguladas por la SBS igual que los bancos.

Trabaje en MiBanco que es el banco de microfinanzas mas grande del Peru, y en Outsourcing Technology desarrolle servicios para sistemas de microfinanzas. Conozco bien el segmento.

---

## P28. Que reportes regulatorios genera el middleware?

**R:** La SBS requiere varios. Los principales son el Anexo 6 que es el detalle de cartera de creditos con clasificacion de riesgo, monto y mora. El Anexo 5 de depositos y obligaciones. El RCD, reporte crediticio de deudores.

Tambien el ITF que es el Impuesto a las Transacciones Financieras que se reporta diariamente, y los reportes de operaciones sospechosas para prevencion de lavado de activos.

Desde el middleware, estos reportes se generan con procesos batch: rutas Camel con timer o cron que extraen datos del core, los transforman al formato SBS, y los envian por SFTP. Tipicamente se ejecutan de madrugada para no afectar la operacion del dia.

---

## P29. Como manejas picos de transacciones en quincena o fin de mes?

**R:** Varias estrategias combinadas:

HPA en OpenShift para autoescalar: si el CPU pasa del 70%, se crean mas replicas del middleware automaticamente.

Las colas AMQ actuan como buffer natural. Si llegan muchas solicitudes, se encolan y se procesan a ritmo sostenible. Aumento los concurrentConsumers dinamicamente para procesar mas rapido.

Circuit breaker para proteger a Bantotal. Si el core esta respondiendo lento por la carga, el circuit breaker evita saturarlo mas y devuelve un fallback controlado.

Throttling para limitar la tasa de mensajes que van a sistemas externos que no escalan.

En MiBanco viviamos estos picos cada quincena. La leccion es que lo peor es dejar que todo el trafico llegue al core de golpe. Las colas como amortiguador y el escalado horizontal del middleware son clave.

---

# PARTE 8: PREGUNTAS SITUACIONALES

---

## P30. Lunes 8am, las consultas de saldo estan fallando. Tu proceso?

**R:** Primero, verifico los logs del middleware. En OpenShift hago oc logs o reviso el logging centralizado. Busco excepciones recientes, especialmente timeouts o errores de conexion.

Si veo timeouts hacia Bantotal, verifico si el core esta arriba. Puede que el proceso batch de cierre de dia se haya extendido y el core este ocupado o caido.

Si no hay errores en logs, verifico el estado de los pods: oc get pods para ver si algun pod esta en CrashLoopBackOff. Reviso eventos con oc describe pod.

Si los pods estan bien, reviso conexiones: test-connection-in-pool en el datasource de JBoss, estado de las colas en AMQ, y conectividad de red hacia Bantotal.

Reviso la Fuse Console para ver si las rutas de consulta de saldo estan activas o detenidas, si hay mensajes acumulados en colas.

Paralelamente, comunico al equipo que estoy investigando. La clave es ir de lo general a lo especifico: logs, pods, conexiones, core. Y documentar para el postmortem.

---

## P31. Te piden integrar una central de riesgo con SLA 99.5% y latencia de 3 segundos. Como la disenas?

**R:** 3 segundos es mucho para una consulta online si el usuario esta esperando. Mi diseno:

**Circuit breaker** alrededor de la llamada. Si falla o esta lenta, activo fallback: usar el ultimo score cacheado del cliente, o continuar sin score y marcarlo para revision.

**Timeout**: 5 segundos maximo en la llamada HTTP. Si no responde, el circuit breaker lo cuenta como fallo.

**Cache**: cacheo los scores por 24 horas. Si ya consulte al cliente hoy, uso cache sin llamar nuevamente.

**Asincrono cuando sea posible**: si el score no es bloqueante para el flujo (por ejemplo, solo ajusta la clasificacion de riesgo), la consulta va asincrona por JMS y proceso el resultado cuando llegue.

**Monitoreo**: alertas cuando el porcentaje de fallback supere un umbral. Con 99.5% SLA, el 0.5% del tiempo la central puede estar caida, asi que el fallback se va a activar y tenemos que estar preparados.

En Camel es circuitBreaker() alrededor del to() al servicio externo, y onFallback() para la logica alternativa.

---

## P32. Como garantizas la consistencia en una transferencia entre cuentas?

**R:** Si las dos cuentas estan en el mismo core Bantotal, uso la operacion BTTransferencias.Crear que maneja la atomicidad internamente.

Si son cuentas en sistemas diferentes, implemento Saga Pattern: debito cuenta origen, intento acreditar la destino, y si falla, reverso el debito. Cada paso tiene su compensacion.

Para evitar inconsistencias por fallos del middleware, cada operacion es idempotente con el ID de transaccion. Registro el estado de cada paso en una tabla de control. Y uso transacciones JMS para que si el middleware cae despues de debitar pero antes de acreditar, el mensaje de compensacion se procese cuando vuelva a levantarse.

Lo critico es nunca perder el registro de que se debito pero no se acredito. La tabla de estados y la auditoria son fundamentales.

---

## P33. Que harias si te piden migrar una integracion de SOAP a REST con Bantotal?

**R:** Primero analizo el impacto. Identifico todos los consumidores de esa integracion SOAP para saber quien se afecta. Reviso si la operacion esta disponible en REST en BTServices, porque no todas lo estan.

Si esta disponible en REST, la migracion en el middleware seria: cambiar el componente de camel-cxf a camel-http, reemplazar los objetos JAXB generados del WSDL por POJOs con Jackson, y ajustar la transformacion de datos. La ruta Camel sigue igual en su logica, solo cambia el endpoint y el formato.

Lo haria de forma gradual: primero implemento la version REST en paralelo, la testeo con un porcentaje del trafico, y cuando estoy seguro, migro todo y retiro la SOAP. Nunca hago un cambio big-bang en produccion financiera.

---

# PARTE 9: PREGUNTAS DE CIERRE

---

## P34. Que te motiva de esta posicion?

**R:** Tres cosas. Primero, seguir en el sector financiero. Es un sector que conozco y donde el impacto es real: cada servicio que desarrollas afecta directamente a personas que necesitan acceso al credito y servicios financieros.

Segundo, seguir creciendo en el stack de Red Hat para integracion. Es un ecosistema solido con mucha demanda en banca latinoamericana y quiero profundizar mi especializacion en esa direccion.

Y tercero, la modalidad remota desde Lima me permite enfocarme 100% en la parte tecnica.

---

## P35. Tienes alguna pregunta para nosotros?

**R:** Si, varias:

- Cuantas rutas Camel manejan actualmente en produccion y cual es el volumen de transacciones diarias?
- Que version de Camel y Fuse estan usando? Ya estan en Camel 3 o 4?
- El equipo de middleware es independiente o comparten responsabilidades con operaciones?
- Tienen algun plan de migracion de servicios SOAP a REST o mantienen ambos?
- Como es el proceso de on-boarding para alguien nuevo en el equipo?
- Que herramientas usan para monitoreo y observabilidad? Prometheus, Grafana, EFK?

---

# CONSEJOS PARA LA ENTREVISTA

1. **Conecta siempre con tu experiencia real**. MiBanco, microfinanzas y grupo financiero son tu carta fuerte
2. **Habla con seguridad** sobre el stack. Conoces las tecnologias, usalo
3. **Usa lenguaje de negocio**: mora, score, SBS, TCEA, cronograma. Demuestra que entiendes el sector
4. **Las preguntas situacionales son las mas importantes**: troubleshooting y diseno es donde tu experiencia real brilla
5. **Haz preguntas inteligentes al final**: demuestra interes genuino y conocimiento tecnico
6. **No sobreexpliques**: responde directo, si quieren mas detalle te lo piden

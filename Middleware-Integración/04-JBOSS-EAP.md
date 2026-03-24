# Red Hat JBoss EAP

---

## 1. Que es JBoss EAP?

**JBoss Enterprise Application Platform** es un servidor de aplicaciones Java EE/Jakarta EE de Red Hat. Es la version empresarial (con soporte) de WildFly.

```
[Cliente] → [JBoss EAP] → [Base de datos]
                │
                ├── Subsistema Web (Undertow)
                ├── Subsistema EJB
                ├── Subsistema JMS (ActiveMQ Artemis)
                ├── Subsistema Datasources
                ├── Subsistema Security (Elytron)
                └── Subsistema Camel (Fuse on EAP)
```

| Concepto | Descripcion |
|----------|-------------|
| **JBoss EAP** | Servidor de aplicaciones Java EE con soporte Red Hat |
| **WildFly** | Version community (upstream) de JBoss EAP |
| **Standalone** | Modo de ejecucion para un solo servidor |
| **Domain** | Modo de ejecucion para multiples servidores gestionados centralmente |
| **Subsystem** | Modulo funcional del servidor (web, ejb, datasource, jms...) |
| **Profile** | Conjunto de subsistemas configurados (default, full, ha, full-ha) |
| **Deployment** | Aplicacion desplegada (WAR, EAR, JAR) |

---

## 2. Estructura de directorios

```
jboss-eap-7.x/
├── bin/
│   ├── standalone.sh          # Iniciar en modo standalone
│   ├── domain.sh              # Iniciar en modo domain
│   ├── jboss-cli.sh           # CLI de administracion
│   └── add-user.sh            # Agregar usuarios admin/app
├── standalone/
│   ├── configuration/
│   │   ├── standalone.xml         # Config standalone basica
│   │   ├── standalone-full.xml    # Config con JMS y todos los subsistemas
│   │   └── standalone-ha.xml      # Config con clustering (HA)
│   ├── deployments/               # Hot-deploy: copiar WAR/EAR aqui
│   ├── log/
│   │   └── server.log
│   └── lib/ext/                   # JARs compartidos (drivers JDBC)
├── domain/
│   ├── configuration/
│   │   ├── domain.xml             # Config del dominio
│   │   └── host.xml               # Config del host
│   └── servers/                   # Instancias de servidor
└── modules/                       # Sistema de modulos (classpath)
    └── system/layers/base/
```

---

## 3. Configuracion de Datasources

```xml
<!-- standalone.xml - Datasource para base de datos del banco -->
<subsystem xmlns="urn:jboss:domain:datasources:6.0">
    <datasources>
        <!-- Datasource NO transaccional -->
        <datasource jndi-name="java:jboss/datasources/BantotalDS"
                    pool-name="BantotalDS"
                    enabled="true"
                    use-java-context="true">
            <connection-url>jdbc:oracle:thin:@dbhost:1521:BTPROD</connection-url>
            <driver>oracle</driver>
            <security>
                <user-name>${vault::ds::username::1}</user-name>
                <password>${vault::ds::password::1}</password>
            </security>
            <pool>
                <min-pool-size>10</min-pool-size>
                <max-pool-size>50</max-pool-size>
                <prefill>true</prefill>
            </pool>
            <validation>
                <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker"/>
                <check-valid-connection-sql>SELECT 1 FROM DUAL</check-valid-connection-sql>
                <validate-on-match>true</validate-on-match>
            </validation>
            <timeout>
                <blocking-timeout-millis>30000</blocking-timeout-millis>
                <idle-timeout-minutes>15</idle-timeout-minutes>
            </timeout>
        </datasource>

        <!-- XA Datasource (transaccional distribuido) -->
        <xa-datasource jndi-name="java:jboss/datasources/BantotalXADS"
                       pool-name="BantotalXADS">
            <xa-datasource-property name="URL">jdbc:oracle:thin:@dbhost:1521:BTPROD</xa-datasource-property>
            <xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource</xa-datasource-class>
            <driver>oracle</driver>
            <xa-pool>
                <min-pool-size>5</min-pool-size>
                <max-pool-size>30</max-pool-size>
            </xa-pool>
        </xa-datasource>

        <drivers>
            <driver name="oracle" module="com.oracle.ojdbc">
                <xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource</xa-datasource-class>
            </driver>
        </drivers>
    </datasources>
</subsystem>
```

---

## 4. JBoss CLI (jboss-cli.sh)

```bash
# Conectar al servidor
./jboss-cli.sh --connect

# Ver estado del servidor
:read-attribute(name=server-state)

# Desplegar aplicacion
deploy /path/to/mi-app.war

# Desplegar con nombre custom
deploy /path/to/mi-app.war --name=middleware-v2.war

# Redesplegar
deploy /path/to/mi-app.war --force

# Quitar deploy
undeploy mi-app.war

# Ver datasources
/subsystem=datasources:read-resource(recursive=true)

# Agregar datasource por CLI
/subsystem=datasources/data-source=NuevoDS:add( \
    jndi-name=java:jboss/datasources/NuevoDS, \
    connection-url="jdbc:postgresql://host:5432/db", \
    driver-name=postgresql, \
    user-name=user, \
    password=pass, \
    min-pool-size=5, \
    max-pool-size=20)

# Testear conexion de datasource
/subsystem=datasources/data-source=BantotalDS:test-connection-in-pool

# Cambiar nivel de log
/subsystem=logging/logger=com.banco.middleware:add(level=DEBUG)

# Ver threads activos
/core-service=platform-mbean/type=threading:read-attribute(name=thread-count)

# Ver memoria
/core-service=platform-mbean/type=memory:read-attribute(name=heap-memory-usage)

# Configurar system property
/system-property=bantotal.host:add(value="10.0.1.50:8080")

# Reload configuracion
:reload
```

---

## 5. Sistema de modulos

JBoss usa un sistema de modulos (en vez de classpath plano) para aislar dependencias.

```xml
<!-- modules/com/oracle/ojdbc/main/module.xml -->
<module name="com.oracle.ojdbc" xmlns="urn:jboss:module:1.9">
    <resources>
        <resource-root path="ojdbc8.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
```

```xml
<!-- jboss-deployment-structure.xml (dentro del WAR/EAR) -->
<!-- Controla que modulos ve tu aplicacion -->
<jboss-deployment-structure>
    <deployment>
        <dependencies>
            <module name="com.oracle.ojdbc"/>
            <module name="org.apache.camel.core" services="export"/>
        </dependencies>
        <exclusions>
            <module name="org.jboss.resteasy"/>  <!-- Excluir modulo del servidor -->
        </exclusions>
    </deployment>
</jboss-deployment-structure>
```

---

## 6. Seguridad con Elytron (EAP 7.x+)

```xml
<!-- Configuracion de seguridad -->
<subsystem xmlns="urn:wildfly:elytron:13.0">
    <!-- Security Domain para autenticacion de la aplicacion -->
    <security-domain name="middleware-domain"
                     default-realm="middleware-realm"
                     permission-mapper="default-permission-mapper">
        <realm name="middleware-realm" role-decoder="groups-to-roles"/>
    </security-domain>

    <!-- JDBC Realm: usuarios en base de datos -->
    <jdbc-realm name="middleware-realm">
        <principal-query sql="SELECT password, role FROM users WHERE username=?"
                         data-source="BantotalDS">
            <clear-password-mapper password-index="1"/>
            <attribute-mapping>
                <attribute to="groups" index="2"/>
            </attribute-mapping>
        </principal-query>
    </jdbc-realm>
</subsystem>
```

---

## 7. Subsistema de Mensajeria (ActiveMQ Artemis integrado)

```xml
<subsystem xmlns="urn:jboss:domain:messaging-activemq:13.0">
    <server name="default">
        <journal pool-files="10" file-size="10485760"/>

        <!-- Cola JMS -->
        <jms-queue name="solicitudes-credito"
                   entries="java:/jms/queue/SolicitudesCredito java:jboss/exported/jms/queue/SolicitudesCredito"/>

        <!-- Topic JMS -->
        <jms-topic name="eventos-transacciones"
                   entries="java:/jms/topic/EventosTransacciones"/>

        <!-- Dead Letter Queue -->
        <address-setting name="#"
                         dead-letter-address="jms.queue.DLQ"
                         expiry-address="jms.queue.ExpiryQueue"
                         max-delivery-attempts="5"
                         redelivery-delay="10000"/>

        <!-- Acceptor remoto (para conexiones externas) -->
        <remote-acceptor name="messaging-remote"
                         socket-binding="messaging"/>

        <!-- Connection Factory -->
        <pooled-connection-factory name="activemq-ra"
                                  entries="java:/JmsXA java:jboss/DefaultJMSConnectionFactory"
                                  connectors="in-vm"
                                  transaction="xa"/>
    </server>
</subsystem>
```

---

## 8. Perfiles y alta disponibilidad

| Perfil | Uso |
|--------|-----|
| **standalone.xml** | Desarrollo: basico sin JMS ni clustering |
| **standalone-full.xml** | Con JMS (messaging) habilitado |
| **standalone-ha.xml** | Con clustering (Infinispan, JGroups) |
| **standalone-full-ha.xml** | Completo: JMS + clustering |

```bash
# Iniciar con perfil full (JMS habilitado)
./standalone.sh -c standalone-full.xml

# Iniciar con perfil HA
./standalone.sh -c standalone-full-ha.xml

# Iniciar con binding a IP especifica
./standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0

# Iniciar con port offset (multiples instancias)
./standalone.sh -Djboss.socket.binding.port-offset=100
```

---

## 9. Tuning y performance

```xml
<!-- Thread pools -->
<subsystem xmlns="urn:jboss:domain:io:3.0">
    <worker name="default" io-threads="8" task-max-threads="64"/>
</subsystem>

<!-- JVM args en standalone.conf -->
<!-- JAVA_OPTS -->
-Xms2g -Xmx4g
-XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m
-XX:+UseG1GC
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/jboss/
-Djboss.as.management.blocking.timeout=600
```

---

## Preguntas de entrevista

### Basicas
1. **Que es JBoss EAP y en que se diferencia de WildFly?**
   - JBoss EAP es la version empresarial (con soporte Red Hat) de WildFly. EAP tiene ciclos de release mas largos, parches de seguridad y certificaciones
2. **Diferencia entre modo standalone y domain?**
   - Standalone: un solo servidor, un archivo de config. Domain: multiples servidores gestionados centralmente por un domain controller
3. **Como despliegas una aplicacion en JBoss?**
   - Copiar WAR/EAR a deployments/, usar CLI (deploy), o la consola web de administracion
4. **Que es un datasource y como se configura?**
   - Recurso JNDI que provee conexiones a BD. Se configura en standalone.xml con driver, URL, pool, validacion
5. **Que es el sistema de modulos de JBoss?**
   - Classpath modular que aisla dependencias. Cada modulo tiene su module.xml con recursos y dependencias

### Intermedias
6. **Como configuras un pool de conexiones optimo?**
   - min-pool-size segun carga base, max-pool-size segun picos, prefill=true, validacion activa, timeouts apropiados
7. **Que es jboss-deployment-structure.xml?**
   - Archivo dentro del WAR/EAR para controlar dependencias de modulos y exclusiones del servidor
8. **Diferencia entre datasource normal y XA datasource?**
   - Normal: transacciones locales (una BD). XA: transacciones distribuidas (multiples recursos con 2PC)
9. **Que es Elytron?**
   - Subsistema de seguridad de EAP 7.x+ que reemplaza PicketBox. Maneja autenticacion, autorizacion, SSL/TLS
10. **Como haces troubleshooting de un JBoss en produccion?**
    - server.log, CLI para metricas, thread dumps (jstack), heap dumps, JMX, ajustar nivel de logging

### Avanzadas
11. **Como configuras clustering en JBoss EAP?**
    - Perfil standalone-full-ha.xml, JGroups para descubrimiento, Infinispan para cache distribuida, mod_cluster/Undertow para balanceo
12. **Como manejas hot-deployment sin downtime?**
    - Rolling update en domain mode, undeploy/deploy en standalone con graceful shutdown
13. **Como securizas credenciales en JBoss?**
    - Elytron credential store (reemplaza Vault en EAP 7.4+), variables de entorno, secrets de OpenShift

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| Que es JBoss EAP? | Servidor de aplicaciones Java EE empresarial de Red Hat |
| EAP vs WildFly? | EAP = WildFly + soporte + parches + certificaciones |
| Standalone vs Domain? | Standalone=1 servidor, Domain=N servidores centralizados |
| Como desplegar? | CLI: deploy app.war, o copiar a deployments/ |
| Datasource? | Recurso JNDI para conexiones BD (pool, validacion) |
| XA Datasource? | Transacciones distribuidas (2PC) con multiples recursos |
| CLI mas usado? | :read-attribute, deploy, /subsystem=...:read-resource |
| Perfiles? | standalone.xml, -full.xml, -ha.xml, -full-ha.xml |
| Seguridad? | Elytron (EAP 7.x+), reemplaza PicketBox |
| Modulos? | Sistema modular de classpath, module.xml |

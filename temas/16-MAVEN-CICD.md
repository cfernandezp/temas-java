# MAVEN, Jenkins y CI/CD

---

## 1. Que es Maven?

Herramienta de **gestion de proyectos Java**: maneja dependencias, compilacion, testing, empaquetado y despliegue.

**Archivo principal: `pom.xml`** (Project Object Model)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>

    <!-- Identificacion del proyecto -->
    <groupId>com.empresa</groupId>      <!-- organizacion -->
    <artifactId>mi-api</artifactId>     <!-- nombre del proyecto -->
    <version>1.0.0</version>            <!-- version -->
    <packaging>jar</packaging>          <!-- jar, war, pom -->

    <!-- Spring Boot parent -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <!-- Dependencias -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

**Comandos Maven:**

```bash
mvn clean               # limpia carpeta target/
mvn compile              # compila el codigo
mvn test                 # ejecuta tests
mvn package              # compila + test + genera JAR/WAR
mvn install              # package + instala en repo local .m2
mvn spring-boot:run      # ejecuta la aplicacion Spring Boot
mvn clean package -DskipTests  # empaquetar sin correr tests
```

**Ciclo de vida Maven:**
```
validate → compile → test → package → verify → install → deploy
```

---

## 2. Starters de Spring Boot

Los **starters** son dependencias preconfiguradas que incluyen todo lo necesario para una funcionalidad.

| Starter | Que incluye |
|---------|-------------|
| `spring-boot-starter-web` | Spring MVC, Tomcat, JSON (Jackson) |
| `spring-boot-starter-data-jpa` | JPA, Hibernate, Spring Data |
| `spring-boot-starter-security` | Spring Security, filtros |
| `spring-boot-starter-test` | JUnit 5, Mockito, MockMvc |
| `spring-boot-starter-webflux` | WebFlux, Netty, reactivo |
| `spring-boot-starter-validation` | Bean Validation (javax.validation) |
| `spring-boot-starter-actuator` | Endpoints de monitoreo (/health, /metrics) |

> **Tip entrevista:** "Los starters simplifican la configuracion. En vez de agregar 5 dependencias para JPA, agrego solo `spring-boot-starter-data-jpa` y Spring Boot autoconfigura Hibernate, DataSource y todo lo necesario."

---

## 3. Scopes de dependencias Maven

| Scope | Cuando esta disponible | Ejemplo |
|-------|----------------------|---------|
| `compile` (default) | Compilacion + runtime + test | spring-boot-starter-web |
| `provided` | Solo compilacion (el servidor lo provee) | lombok, servlet-api |
| `runtime` | Solo runtime (no compilacion) | driver de BD (postgresql) |
| `test` | Solo para tests | spring-boot-starter-test |

---

## 4. Jenkins

Servidor de **automatizacion CI/CD**. Ejecuta pipelines que compilan, testean y despliegan tu codigo.

```groovy
// Jenkinsfile (Pipeline declarativo)
pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'develop', url: 'https://repo.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml' // publicar resultados
                }
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        stage('Deploy') {
            when {
                branch 'main'  // solo deploya desde main
            }
            steps {
                sh 'docker build -t mi-app .'
                sh 'docker push registry.empresa.com/mi-app'
            }
        }
    }
    post {
        failure {
            mail to: 'equipo@empresa.com', subject: 'Build fallido'
        }
    }
}
```

**Flujo tipico Jenkins:**
```
Push a Git → Jenkins detecta cambio → Build → Test → Package → Deploy
```

> **Tip entrevista:** "Jenkins ejecuta el pipeline automaticamente en cada push. Si los tests fallan, el pipeline se detiene y notifica al equipo. Solo se deploya desde la rama main."

---

## 5. Desplegar API: on-premise vs cloud

| Opcion | Que es | Cuando usar |
|--------|--------|-------------|
| **On-premise** | Servidor fisico en tu empresa | Datos sensibles, regulaciones estrictas |
| **Cloud (AWS, Azure, GCP)** | Servidores en la nube | Escalabilidad, pagos por uso |
| **Docker + Kubernetes** | Contenedores orquestados | Microservicios, alta disponibilidad |
| **PaaS (Heroku, Railway)** | Plataforma gestionada | MVPs, proyectos pequenos |

**Desplegar en servidor on-premise:**
```bash
# 1. Generar JAR
mvn clean package

# 2. Copiar al servidor
scp target/app.jar usuario@servidor:/opt/app/

# 3. Ejecutar
java -jar /opt/app/app.jar --spring.profiles.active=prod

# 4. Configurar como servicio (systemd)
# /etc/systemd/system/mi-app.service
```

---

## RESPUESTAS RAPIDAS - MAVEN & CI/CD

| Pregunta | Respuesta |
|----------|-----------|
| Que es Maven? | Herramienta de gestion de proyectos: dependencias, build, testing, deploy |
| Archivo principal de Maven? | `pom.xml` (Project Object Model) |
| Que es un starter? | Dependencia preconfigurada de Spring Boot que incluye todo para una funcionalidad |
| @SpringBootApplication? | Combina @Configuration + @EnableAutoConfiguration + @ComponentScan. Es la clase principal |
| Que es Jenkins? | Servidor de automatizacion CI/CD que ejecuta pipelines de build/test/deploy |
| Que es CI/CD? | CI: build + test automatico en cada push. CD: deploy automatico |
| On-premise vs Cloud? | On-premise: servidor propio (datos sensibles). Cloud: servidores en la nube (escalable) |
| mvn package vs mvn install? | package genera JAR. install ademas lo copia al repo local .m2 |

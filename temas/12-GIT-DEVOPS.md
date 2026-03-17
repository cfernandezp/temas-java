# GIT y DevOps

---

## 1. Comandos Git esenciales

```bash
# Basicos
git init                          # iniciar repositorio
git clone <url>                   # clonar repositorio
git status                        # ver estado actual
git add .                         # agregar todos los cambios
git commit -m "mensaje"           # crear commit
git push origin main              # subir al remoto
git pull origin main              # traer cambios del remoto

# Ramas
git branch                        # listar ramas
git branch feature/login          # crear rama
git checkout feature/login        # cambiar a rama
git checkout -b feature/login     # crear + cambiar (atajo)
git merge feature/login           # fusionar rama en la actual
git branch -d feature/login       # eliminar rama

# Historial
git log --oneline                 # historial resumido
git diff                          # ver cambios no staged
git blame archivo.java            # quien modifico cada linea

# Deshacer
git reset --soft HEAD~1           # deshacer ultimo commit (mantiene cambios)
git reset --hard HEAD~1           # deshacer ultimo commit (PIERDE cambios)
git stash                         # guardar cambios temporalmente
git stash pop                     # recuperar cambios guardados
git revert <commit>               # crear commit que deshace otro (seguro)
```

---

## 2. Git Flow (flujo de trabajo)

```
main (produccion)
  └── develop (desarrollo)
        ├── feature/login    ← nuevas funcionalidades
        ├── feature/carrito
        ├── bugfix/fix-login ← correcciones
        └── release/v1.0     ← preparar release

hotfix/critical-bug ← fix urgente desde main
```

**Flujo tipico:**
1. Crear rama `feature/` desde `develop`
2. Desarrollar y hacer commits
3. Crear **Pull Request** hacia `develop`
4. Code review + aprobacion
5. Merge a `develop`
6. Cuando se prepara release: rama `release/` desde `develop`
7. Merge `release/` a `main` + tag de version

> **Tip entrevista:** "En mi equipo usamos Git Flow con PRs obligatorios y code review. Cada PR debe pasar CI (tests + lint) antes de poder hacer merge."

---

## 3. git reset vs git revert

| Comando | Que hace | Seguro? |
|---------|----------|---------|
| `git reset --soft HEAD~1` | Deshace commit, mantiene cambios staged | Cuidado (reescribe historia) |
| `git reset --hard HEAD~1` | Deshace commit, PIERDE cambios | Peligroso |
| `git revert <commit>` | Crea nuevo commit que deshace los cambios | Si (no reescribe historia) |

> **Tip entrevista:** "En ramas compartidas siempre uso `revert` porque no reescribe la historia. `reset` solo en mi rama local antes de push."

---

## 4. Merge conflicts

```bash
# Cuando hay conflicto:
git merge feature/login
# CONFLICT en archivo.java

# El archivo muestra:
<<<<<<< HEAD
    codigo de tu rama
=======
    codigo de la otra rama
>>>>>>> feature/login

# Resuelves manualmente, luego:
git add archivo.java
git commit -m "resolve merge conflict"
```

---

## 5. CI/CD (Integracion Continua / Despliegue Continuo)

```
CI (Integracion Continua):
  Push → Build → Tests → Lint → Artifact
  Herramientas: Jenkins, GitHub Actions, GitLab CI

CD (Despliegue Continuo):
  Artifact → Deploy Staging → Tests E2E → Deploy Produccion
  Herramientas: ArgoCD, Kubernetes, AWS CodePipeline
```

**Pipeline tipico (GitHub Actions):**
```yaml
name: CI/CD
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: mvn clean test        # compilar + tests
      - run: mvn package            # generar JAR
      - run: docker build -t app .  # crear imagen Docker
```

---

## 6. Docker basico

```dockerfile
# Dockerfile para Spring Boot
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker build -t mi-app .          # construir imagen
docker run -p 8080:8080 mi-app    # ejecutar contenedor
docker-compose up -d              # levantar multiples servicios
```

---

## 7. Conceptos DevOps clave

| Concepto | Que es |
|----------|--------|
| **Uptime** | Porcentaje de tiempo que el sistema esta disponible (ej: 99.9% = 8.7h down/ano) |
| **Escalabilidad horizontal** | Agregar mas instancias del servicio |
| **Escalabilidad vertical** | Agregar mas CPU/RAM a la maquina |
| **Load Balancer** | Distribuye trafico entre multiples instancias |
| **Health Check** | Endpoint `/actuator/health` para verificar que el servicio esta vivo |
| **Blue-Green Deploy** | 2 ambientes: uno activo (blue), otro con nueva version (green). Switch instantaneo |
| **Canary Deploy** | Enviar un % del trafico a la nueva version para validar antes de full deploy |

---

## 8. Comandos Git detallados

```bash
# git clone: opciones utiles
git clone <url>                           # clonar todo el repo
git clone <url> --branch feature/login    # clonar solo una rama especifica
git clone <url> --depth 1                 # clonar solo el ultimo commit (rapido)
git clone <url> --single-branch -b main   # solo rama main, sin historial de otras

# git stash: guardar cambios temporalmente
git stash                                 # guardar cambios sin commit
git stash save "mensaje descriptivo"      # con mensaje
git stash list                            # ver stashes guardados
git stash pop                             # recuperar ultimo stash y eliminarlo
git stash apply stash@{0}                 # aplicar stash especifico sin eliminarlo
git stash drop stash@{0}                  # eliminar stash especifico
git stash clear                           # eliminar todos los stashes

# git add: opciones
git add archivo.java                      # agregar archivo especifico
git add .                                 # agregar TODO (nuevo, modificado, eliminado)
git add -A                                # igual que "git add ." desde la raiz
git add -p                                # agregar por partes (interactivo, elige hunks)
git add *.java                            # agregar todos los .java
git add src/                              # agregar todo dentro de src/
```

---

## 9. Bitbucket vs GitHub

| Aspecto | GitHub | Bitbucket |
|---------|--------|-----------|
| Propietario | Microsoft | Atlassian |
| Repos privados gratis | Si | Si |
| Integracion CI/CD | GitHub Actions | Bitbucket Pipelines |
| Integracion herramientas | Marketplace extenso | Jira, Confluence, Trello |
| Code review | Pull Requests | Pull Requests |
| Cuando se usa | Open source, startups, comunidad | Empresas que usan Jira/Atlassian |

```yaml
# Bitbucket Pipeline (equivalente a GitHub Actions)
# bitbucket-pipelines.yml
image: maven:3.8-openjdk-17

pipelines:
  default:
    - step:
        name: Build and Test
        caches:
          - maven
        script:
          - mvn clean test
          - mvn package
  branches:
    main:
      - step:
          name: Deploy to Production
          deployment: production
          script:
            - mvn package -DskipTests
            - docker build -t app .
            - docker push registry/app:latest
```

---

## 10. Pull Request y Code Review

```
Flujo de Pull Request (PR):

1. Crear rama feature desde develop
   git checkout -b feature/nueva-funcionalidad develop

2. Desarrollar + commits
   git add . && git commit -m "feat: agregar endpoint de usuarios"

3. Push al remoto
   git push origin feature/nueva-funcionalidad

4. Crear Pull Request (en GitHub/Bitbucket)
   - Titulo descriptivo
   - Descripcion de que hace y por que
   - Asignar reviewers

5. Code Review (reviewer revisa):
   - Logica de negocio correcta?
   - Sigue los patrones del proyecto?
   - Tests incluidos?
   - Sin vulnerabilidades de seguridad?
   - Codigo limpio y legible?

6. CI pipeline pasa (tests + lint + build)

7. Aprobar + Merge a develop
   - Squash merge: todos los commits en 1 (limpia historial)
   - Merge commit: mantiene historial completo
   - Rebase: historial lineal

8. Eliminar rama feature
```

> **Tip entrevista:** "En mi equipo, todo cambio pasa por PR con al menos 1 aprobacion. El CI debe pasar (tests + lint) antes de poder hacer merge. Uso squash merge para mantener el historial limpio."

---

## 11. Refactoring y Code Smells

**Refactoring**: mejorar la estructura interna del codigo SIN cambiar su comportamiento externo.

```java
// CODE SMELLS (senales de que el codigo necesita refactoring):

// 1. METODO LARGO (Long Method) → extraer metodos
// ANTES:
public void procesarPedido(Pedido pedido) {
    // 50 lineas de validacion...
    // 30 lineas de calculo de precio...
    // 20 lineas de envio de email...
}
// DESPUES:
public void procesarPedido(Pedido pedido) {
    validar(pedido);
    calcularPrecio(pedido);
    enviarConfirmacion(pedido);
}

// 2. CLASE DIOS (God Class) → dividir responsabilidades
// ANTES: UsuarioService con 2000 lineas que hace todo
// DESPUES: UsuarioService, AuthService, EmailService, ReporteService

// 3. CODIGO DUPLICADO → extraer metodo comun o clase base
// ANTES: misma validacion de email en 5 clases
// DESPUES: EmailValidator reutilizable

// 4. NOMBRES POBRES → renombrar con significado
// ANTES:
int d; // dias
String s; // nombre del servicio
public void proc(Object o) { ... }
// DESPUES:
int diasHabiles;
String nombreServicio;
public void procesarPago(Pago pago) { ... }

// 5. MAGIC NUMBERS → usar constantes
// ANTES:
if (edad > 18) { ... }
if (intentos > 3) { ... }
// DESPUES:
private static final int EDAD_MINIMA = 18;
private static final int MAX_INTENTOS = 3;

// 6. FEATURE ENVY → mover metodo a la clase correcta
// ANTES: PedidoService calcula el descuento accediendo a 5 campos de Producto
// DESPUES: Producto.calcularDescuento() - el metodo vive donde estan los datos
```

> **Tip entrevista:** "Refactoring es mejorar el codigo sin cambiar funcionalidad. Lo hago continuamente: extraigo metodos largos, elimino duplicacion, y renombro para claridad. Los code smells mas comunes que encuentro son metodos largos y clases con demasiadas responsabilidades."

---

## RESPUESTAS RAPIDAS - GIT & DEVOPS

| Pregunta | Respuesta |
|----------|-----------|
| git reset vs revert? | reset reescribe historia (local). revert crea commit inverso (seguro para ramas compartidas) |
| Que es Git Flow? | Flujo con ramas main, develop, feature, release, hotfix |
| Que es CI/CD? | CI: build + test automatico en cada push. CD: deploy automatico a produccion |
| Que es Docker? | Contenedor que empaqueta la app con todas sus dependencias |
| Escalabilidad horizontal? | Agregar mas instancias del servicio (no mas CPU a una maquina) |
| Que es un Load Balancer? | Distribuye trafico entre multiples instancias del servicio |
| Como resuelves merge conflicts? | Editar manualmente el archivo, elegir los cambios correctos, commit |
| Clonar solo una rama? | `git clone --branch feature/x --single-branch <url>` |
| git stash para que? | Guardar cambios temporalmente sin commit. `stash pop` para recuperar |
| GitHub vs Bitbucket? | GitHub: comunidad, Actions. Bitbucket: empresas con Jira/Atlassian |
| Que es code review? | Revision de codigo por peers antes de merge. Verifica calidad, tests, seguridad |
| Que es refactoring? | Mejorar estructura del codigo sin cambiar comportamiento externo |
| Code smells comunes? | Metodo largo, clase dios, codigo duplicado, nombres pobres, magic numbers |

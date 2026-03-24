# OpenShift y Kubernetes

---

## 1. Que es Kubernetes?

Plataforma de **orquestacion de contenedores** que automatiza el despliegue, escalado y gestion de aplicaciones containerizadas.

```
[Cluster Kubernetes]
├── Master Node (Control Plane)
│   ├── API Server          ← Toda comunicacion pasa por aqui
│   ├── etcd                ← Base de datos del estado del cluster
│   ├── Scheduler           ← Decide en que nodo correr cada pod
│   └── Controller Manager  ← Mantiene estado deseado = estado actual
│
├── Worker Node 1
│   ├── kubelet             ← Agente que gestiona pods en el nodo
│   ├── kube-proxy          ← Networking y balanceo
│   ├── Pod (middleware-v1) ← Contenedor(es) corriendo
│   └── Pod (middleware-v2)
│
└── Worker Node 2
    ├── kubelet
    ├── kube-proxy
    └── Pod (bantotal-adapter)
```

**OpenShift** = Kubernetes + extras de Red Hat (builds, routes, imagen registry, consola web, seguridad reforzada).

---

## 2. Conceptos clave

| Concepto | Descripcion |
|----------|-------------|
| **Pod** | Unidad minima. Uno o mas contenedores que comparten red y almacenamiento |
| **Deployment** | Gestiona ReplicaSets y permite rolling updates de pods |
| **Service** | Abstraccion de red que expone pods con IP estable y balanceo |
| **ConfigMap** | Almacena configuracion como key-value (no sensible) |
| **Secret** | Almacena datos sensibles (passwords, tokens, certificados) codificados en base64 |
| **Namespace** | Aislamiento logico dentro del cluster (por equipo o ambiente) |
| **Ingress** | Expone servicios HTTP/HTTPS al exterior (reglas de ruteo) |
| **PersistentVolume (PV)** | Almacenamiento persistente independiente del pod |
| **PersistentVolumeClaim (PVC)** | Solicitud de almacenamiento por parte del pod |
| **ReplicaSet** | Garantiza que N replicas del pod esten corriendo |
| **StatefulSet** | Como Deployment pero para apps con estado (Kafka, BD) |
| **DaemonSet** | Un pod por nodo (logging, monitoring) |
| **Job / CronJob** | Tareas batch / tareas programadas |
| **HPA** | Horizontal Pod Autoscaler: escala pods segun CPU/memoria |

---

## 3. OpenShift - Conceptos adicionales

| Concepto OpenShift | Equivalente K8s | Descripcion |
|--------------------|-----------------|-------------|
| **Route** | Ingress | Expone servicios al exterior con URL amigable, TLS integrado |
| **BuildConfig** | - | Define como construir imagen desde codigo (S2I, Dockerfile) |
| **ImageStream** | - | Referencia a imagenes con tags y triggers automaticos |
| **DeploymentConfig** | Deployment | (Legacy) Similar a Deployment con triggers y hooks |
| **Project** | Namespace | Namespace con permisos y cuotas adicionales |
| **S2I (Source-to-Image)** | - | Construye imagen desde codigo fuente sin Dockerfile |
| **oc** | kubectl | CLI de OpenShift (superset de kubectl) |
| **Operator** | Operator | Patron para gestionar apps complejas (AMQ Operator, Fuse Operator) |

---

## 4. Manifiestos YAML esenciales

### Deployment

```yaml
# deployment.yaml - Middleware Camel
apiVersion: apps/v1
kind: Deployment
metadata:
  name: middleware-creditos
  namespace: middleware-prod
  labels:
    app: middleware-creditos
    version: v2.1.0
spec:
  replicas: 3
  selector:
    matchLabels:
      app: middleware-creditos
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1          # Crear 1 pod extra durante update
      maxUnavailable: 0     # Nunca tener menos de 3 pods
  template:
    metadata:
      labels:
        app: middleware-creditos
        version: v2.1.0
    spec:
      containers:
        - name: middleware
          image: registry.banco.com/middleware/creditos:2.1.0
          ports:
            - containerPort: 8080
              name: http
            - containerPort: 8778
              name: jolokia    # Monitoreo JMX/Hawtio
          env:
            - name: BANTOTAL_HOST
              valueFrom:
                configMapKeyRef:
                  name: bantotal-config
                  key: host
            - name: BANTOTAL_TOKEN
              valueFrom:
                secretKeyRef:
                  name: bantotal-secrets
                  key: token
            - name: JAVA_OPTS
              value: "-Xms512m -Xmx1024m -XX:+UseG1GC"
          resources:
            requests:
              cpu: "500m"       # 0.5 cores
              memory: "512Mi"
            limits:
              cpu: "1000m"      # 1 core
              memory: "1024Mi"
          readinessProbe:
            httpGet:
              path: /health/ready
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /health/live
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 15
          volumeMounts:
            - name: config-volume
              mountPath: /deployments/config
      volumes:
        - name: config-volume
          configMap:
            name: middleware-config
```

### Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: middleware-creditos
  namespace: middleware-prod
spec:
  selector:
    app: middleware-creditos
  ports:
    - name: http
      port: 8080
      targetPort: 8080
  type: ClusterIP   # Solo accesible dentro del cluster
```

### Route (OpenShift)

```yaml
apiVersion: route.openshift.io/v1
kind: Route
metadata:
  name: middleware-creditos
  namespace: middleware-prod
spec:
  host: middleware-creditos.apps.banco.com
  to:
    kind: Service
    name: middleware-creditos
  port:
    targetPort: http
  tls:
    termination: edge                # TLS en el router
    insecureEdgeTerminationPolicy: Redirect
```

### ConfigMap y Secret

```yaml
# ConfigMap
apiVersion: v1
kind: ConfigMap
metadata:
  name: bantotal-config
data:
  host: "bantotal-core.banco.internal:8080"
  canal: "MIDDLEWARE"
  timeout: "30000"
  application.properties: |
    camel.component.jms.connection-factory=#connectionFactory
    bantotal.api.base-url=http://bantotal-core:8080/BTServices/api

---
# Secret
apiVersion: v1
kind: Secret
metadata:
  name: bantotal-secrets
type: Opaque
data:
  token: YWJjMTIzZGVmNDU2    # base64 encoded
  password: c2VjcmV0MTIz      # base64 encoded
```

### HPA (Horizontal Pod Autoscaler)

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: middleware-creditos-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: middleware-creditos
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

---

## 5. Comandos esenciales

### kubectl / oc

```bash
# --- BASICOS ---
kubectl get pods -n middleware-prod              # Listar pods
kubectl get pods -o wide                         # Pods con nodo e IP
kubectl get deployments                          # Listar deployments
kubectl get services                             # Listar servicios
kubectl get all                                  # Todo en el namespace

# --- DESCRIBIR Y DIAGNOSTICAR ---
kubectl describe pod middleware-creditos-abc123   # Detalle del pod (eventos, estado)
kubectl logs middleware-creditos-abc123            # Ver logs
kubectl logs middleware-creditos-abc123 -f         # Seguir logs (tail -f)
kubectl logs middleware-creditos-abc123 --previous # Logs del contenedor anterior (crash)

# --- DEPLOY ---
kubectl apply -f deployment.yaml                  # Crear/actualizar recursos
kubectl delete -f deployment.yaml                 # Eliminar recursos
kubectl set image deployment/middleware-creditos \
    middleware=registry.banco.com/middleware:2.2.0 # Actualizar imagen

# --- ESCALAR ---
kubectl scale deployment middleware-creditos --replicas=5

# --- DEBUGGING ---
kubectl exec -it middleware-creditos-abc123 -- /bin/bash  # Shell en el pod
kubectl port-forward svc/middleware-creditos 8080:8080     # Acceder local

# --- ROLLBACK ---
kubectl rollout status deployment/middleware-creditos     # Estado del rollout
kubectl rollout history deployment/middleware-creditos    # Historial
kubectl rollout undo deployment/middleware-creditos       # Rollback al anterior

# --- OPENSHIFT ESPECIFICOS ---
oc new-project middleware-dev                    # Crear proyecto
oc new-app --image-stream=fuse7-java-openshift \ # Deploy con S2I
    --code=https://git.banco.com/middleware.git
oc start-build middleware-creditos               # Iniciar build
oc get routes                                    # Ver rutas (URLs)
oc expose svc/middleware-creditos                # Crear ruta automatica
oc get builds                                    # Ver builds
oc adm top pods                                  # Consumo de recursos
```

---

## 6. Fuse on OpenShift

```yaml
# BuildConfig para Fuse/Camel con S2I
apiVersion: build.openshift.io/v1
kind: BuildConfig
metadata:
  name: middleware-creditos
spec:
  source:
    type: Git
    git:
      uri: https://git.banco.com/middleware/creditos.git
      ref: main
  strategy:
    type: Source
    sourceStrategy:
      from:
        kind: ImageStreamTag
        namespace: openshift
        name: fuse7-java-openshift:1.12  # Imagen base Fuse
      env:
        - name: MAVEN_ARGS
          value: "-Pprod package -DskipTests"
  output:
    to:
      kind: ImageStreamTag
      name: middleware-creditos:latest
  triggers:
    - type: ConfigChange
    - type: ImageChange
```

---

## 7. Networking y comunicacion entre servicios

```
[Pod A: middleware-creditos]
    │
    │  http://bantotal-adapter:8080/api  (Service DNS)
    │  (Service name = DNS name dentro del cluster)
    │
    ▼
[Pod B: bantotal-adapter]
    │
    │  http://bantotal-core.banco.internal:8080
    │  (Servicio externo al cluster)
    │
    ▼
[Bantotal Core]
```

```yaml
# Para servicios externos al cluster
apiVersion: v1
kind: Service
metadata:
  name: bantotal-external
spec:
  type: ExternalName
  externalName: bantotal-core.banco.internal
```

---

## Preguntas de entrevista

### Basicas
1. **Que es Kubernetes?**
   - Plataforma de orquestacion de contenedores: automatiza deploy, escalado, self-healing de apps containerizadas
2. **Que es OpenShift y en que se diferencia de Kubernetes?**
   - OpenShift = Kubernetes + Routes, S2I builds, ImageStreams, consola web, seguridad reforzada (Red Hat)
3. **Que es un Pod?**
   - Unidad minima de deploy. Uno o mas contenedores que comparten red (IP) y almacenamiento
4. **Diferencia entre Deployment y StatefulSet?**
   - Deployment: apps sin estado (middleware). StatefulSet: apps con estado y identidad fija (Kafka, BD)
5. **Que es un Service en Kubernetes?**
   - Abstraccion de red que provee IP estable, DNS y balanceo a un conjunto de pods

### Intermedias
6. **Diferencia entre Route (OpenShift) e Ingress (K8s)?**
   - Route: nativo de OpenShift, TLS integrado, wildcard DNS. Ingress: estandar K8s, necesita ingress controller
7. **Como configuras variables de entorno sensibles?**
   - Secrets (base64). Montarlos como env vars o volumenes. Nunca en ConfigMaps o hardcoded
8. **Que son readinessProbe y livenessProbe?**
   - readiness: determina si el pod puede recibir trafico. liveness: determina si el pod esta vivo (restart si falla)
9. **Como haces rolling update sin downtime?**
   - strategy: RollingUpdate, maxSurge=1, maxUnavailable=0, readinessProbe bien configurado
10. **Que es S2I (Source-to-Image)?**
    - Build strategy de OpenShift: toma codigo fuente + imagen base y genera imagen de contenedor sin Dockerfile

### Avanzadas
11. **Como gestionas configuracion entre ambientes (dev/qa/prod)?**
    - ConfigMaps y Secrets por namespace, Kustomize para overlays, o Helm charts con values por ambiente
12. **Como monitoreas pods de Camel/Fuse en OpenShift?**
    - Prometheus + Grafana, Jolokia (JMX), Hawtio (Fuse Console), logs centralizados con EFK/Loki
13. **Como implementas CI/CD para middleware en OpenShift?**
    - Jenkins/Tekton pipeline: build → test → push image → deploy dev → promote qa → promote prod
14. **Como limitas recursos en un namespace?**
    - ResourceQuota (limites totales) y LimitRange (limites por pod/container)

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| Que es K8s? | Orquestador de contenedores |
| OpenShift vs K8s? | OpenShift = K8s + Routes + S2I + seguridad Red Hat |
| Pod? | Unidad minima: contenedor(es) con red y storage compartido |
| Deployment? | Gestiona replicas y rolling updates de pods |
| Service? | IP estable + DNS + balanceo para un grupo de pods |
| ConfigMap vs Secret? | ConfigMap=config no sensible, Secret=datos sensibles (base64) |
| Route vs Ingress? | Route=OpenShift nativo, Ingress=K8s estandar |
| readiness vs liveness? | readiness=puede recibir trafico, liveness=esta vivo |
| S2I? | Build de imagen desde codigo sin Dockerfile |
| HPA? | Escala pods automaticamente segun CPU/memoria |
| oc vs kubectl? | oc es superset de kubectl para OpenShift |

# GESTION DE INCIDENTES, RIESGOS Y RELEASE

---

## 1. Gestion de incidentes en produccion

### Clasificacion de incidentes

| Severidad | Descripcion | Ejemplo Apuestas | SLA respuesta |
|-----------|-------------|-------------------|--------------|
| **P1 - Critico** | Plataforma caida, no se puede apostar/depositar | Plataforma no carga, pagos no procesan | < 15 min |
| **P2 - Alto** | Feature principal degradada | Apuestas en vivo lentas, cuotas no actualizan | < 1 hora |
| **P3 - Medio** | Feature secundaria afectada | Historial no carga, notificaciones no llegan | < 4 horas |
| **P4 - Bajo** | Cosmetico o menor | Texto cortado en un boton, color incorrecto | Siguiente sprint |

### Proceso de gestion de incidentes

```
1. DETECCION     → Monitoreo (Grafana), reporte de usuario, alerta automatica
      ↓
2. TRIAJE        → PO + On-call clasifican severidad y asignan
      ↓
3. COMUNICACION  → Notificar a stakeholders y usuarios (si aplica)
      ↓
4. RESOLUCION    → Equipo investiga y aplica fix
      ↓
5. VERIFICACION  → QA valida que el fix funciona
      ↓
6. POST-MORTEM   → Analisis de causa raiz y acciones preventivas
```

### Rol del PO durante un incidente

| Fase | Que hace el PO |
|------|---------------|
| **Deteccion** | Entiende el impacto en usuarios y negocio |
| **Triaje** | Decide la prioridad vs el trabajo del sprint |
| **Comunicacion** | Informa a stakeholders de negocio (no tecnicos) |
| **Resolucion** | Apoya al equipo priorizando el fix sobre el sprint |
| **Post-mortem** | Asegura que las acciones preventivas entren al backlog |

> **Tip entrevista:** "En MiBanco viviamos incidentes del core bancario cada semana. Aprendi a mantener la calma, comunicar con claridad y no entrar en panico. Lo primero es entender el impacto en el usuario, no buscar culpables."

---

## 2. Post-mortem (Analisis post-incidente)

### Template de Post-Mortem

```
# Post-Mortem: [Titulo del incidente]
Fecha: [DD/MM/YYYY]
Duracion: [tiempo total de afectacion]
Severidad: P1/P2/P3

## Resumen
[Que paso en 2-3 oraciones]

## Impacto
- Usuarios afectados: [numero]
- Revenue perdido: [estimado]
- Tickets de soporte: [numero]

## Timeline
- HH:MM - Se detecta el problema
- HH:MM - Se notifica al equipo
- HH:MM - Se identifica la causa
- HH:MM - Se aplica el fix
- HH:MM - Se verifica la solucion
- HH:MM - Se da por cerrado

## Causa raiz
[Descripcion tecnica y de proceso]

## Que salio bien
- [Algo que funciono correctamente]

## Que salio mal
- [Algo que fallo en el proceso]

## Acciones preventivas
| Accion | Responsable | Fecha limite |
|--------|-------------|-------------|
| [Accion 1] | [Persona] | [Fecha] |
| [Accion 2] | [Persona] | [Fecha] |

## Lecciones aprendidas
[Que aprendimos como equipo]
```

**Regla de oro:** Post-mortem sin culpas (blameless). El objetivo es mejorar el sistema, no castigar personas.

---

## 3. Gestion de riesgos del producto

### Matriz de riesgos

```
              Alta Probabilidad
                    |
   MITIGAR          |    EVITAR / ESCALAR
   (Plan B)         |    (Accion inmediata)
   Integracion      |    Caida en evento
   con proveedor    |    deportivo grande
   falla            |    (ej: Mundial)
                    |
  ──────────────────┼──────────────────
                    |
   ACEPTAR          |    MONITOREAR
   (Riesgo bajo)    |    (Watch closely)
   Cambio menor     |    Regulacion
   en UI que no     |    nueva que
   gusta            |    podria impactar
                    |
              Baja Probabilidad

  Bajo Impacto  ←──────→  Alto Impacto
```

### Tipos de riesgo que el PO gestiona

| Tipo | Ejemplo | Mitigacion |
|------|---------|-----------|
| **Tecnico** | Integracion con proveedor de cuotas falla | Spike previo, proveedor backup |
| **De negocio** | Feature no genera el impacto esperado | MVP + A/B test antes de invertir |
| **Regulatorio** | Nuevo requisito de MINCETUR | Monitoreo constante, compliance en equipo |
| **De mercado** | Competidor lanza feature similar antes | Discovery continuo, velocidad de entrega |
| **Operacional** | Pico de trafico en final de campeonato | Load testing, escalamiento automatico |
| **De personas** | Dev clave se va del equipo | Documentacion, pair programming, cross-training |

---

## 4. Release Management

### Estrategias de release

| Estrategia | Como funciona | Cuando usarla |
|-----------|---------------|---------------|
| **Big Bang** | Todo se lanza de una vez | Casi nunca. Alto riesgo |
| **Feature Flags** | Codigo desplegado pero oculto, se activa gradualmente | Features con riesgo medio |
| **Canary Release** | 5% → 20% → 50% → 100% de usuarios | Features criticas (pagos, apuestas) |
| **Blue/Green** | Dos ambientes identicos, switch instantaneo | Cambios de infraestructura |
| **A/B Test** | 50/50 con metricas, se elige el ganador | Optimizaciones de UX |

### Proceso de release para el PO

```
1. PLANIFICACION   → Que se incluye en el release?
      ↓
2. VALIDACION QA   → Todas las historias pasaron QA?
      ↓
3. RELEASE NOTES   → Documentar que cambia para soporte/stakeholders
      ↓
4. GO/NO-GO        → Decision final: lanzamos o no?
      ↓
5. DEPLOY          → Despliegue (idealmente automatizado CI/CD)
      ↓
6. SMOKE TEST      → Verificacion rapida post-deploy
      ↓
7. MONITOREO       → Vigilar metricas y alertas las primeras horas
      ↓
8. COMUNICACION    → Notificar a stakeholders que se lanzo
```

### Checklist Go/No-Go

```
[ ] Todas las historias del release estan en "Done"
[ ] QA aprobo todas las historias
[ ] No hay bugs P1/P2 abiertos relacionados
[ ] Performance testing pasado (si aplica)
[ ] Rollback plan definido y probado
[ ] Stakeholders informados
[ ] Soporte tiene la documentacion del cambio
[ ] Monitoreo configurado para las nuevas features
[ ] No hay eventos deportivos criticos en las proximas 2 horas
    (NUNCA lanzar durante un partido importante)
```

---

## 5. Rollback: cuando y como revertir

### Criterios para hacer rollback

| Criterio | Umbral |
|----------|--------|
| Error rate | > 5% de requests fallan |
| Tiempo de respuesta | > 3x el baseline |
| Conversion | Cae mas del 20% vs baseline |
| Tickets de soporte | > 10 en la primera hora |
| Revenue | GGR cae mas del 15% |

### Plan de rollback

```
1. DECISION:     El PO autoriza el rollback (con input de dev/ops)
2. EJECUCION:    Revertir el deploy a la version anterior
3. VERIFICACION: Smoke test de la version anterior
4. COMUNICACION: Notificar al equipo y stakeholders
5. ANALISIS:     Post-mortem para entender que fallo
6. PLAN:         Corregir y re-planificar el release
```

> **Tip entrevista:** "Prefiero un rollback rapido a dejar una feature rota en produccion. Es mejor revertir en 10 minutos que perder usuarios durante horas. El orgullo de 'ya lo lanzamos' no vale mas que la confianza del usuario."

---

## 6. Bugs vs Features: como priorizar

### Framework de priorizacion de bugs

```
P1 (Critico)  → Arreglar AHORA, parar el sprint si es necesario
P2 (Alto)     → Incluir en el sprint actual si hay capacidad
P3 (Medio)    → Priorizar para el proximo sprint
P4 (Bajo)     → Backlog, hacer cuando haya tiempo
```

### Regla del PO para balance bugs/features

```
Escenario ideal:
- 70% Features nuevas / mejoras
- 20% Deuda tecnica
- 10% Bugs

Escenario post-release problematico:
- 40% Features
- 20% Deuda tecnica
- 40% Bugs (hasta estabilizar)

Regla: Si los bugs consumen >30% del sprint consistentemente,
hay un problema sistemico de calidad que resolver (no solo
"mas testing", sino revisar DoD, code review, ambientes de QA)
```

---

## 7. Continuidad del negocio y plan de contingencia

### Escenarios criticos en apuestas

| Escenario | Impacto | Plan de contingencia |
|-----------|---------|---------------------|
| **Plataforma caida** | No se puede apostar, perdida de GGR | Failover automatico, pagina de status |
| **Proveedor de cuotas caido** | No hay cuotas en vivo | Proveedor backup, modo pre-match only |
| **Pasarela de pago caida** | No se puede depositar | Multiples pasarelas, mensaje "usa otro metodo" |
| **Fraude masivo** | Perdida financiera, riesgo legal | Reglas de fraude automaticas, bloqueo de cuentas |
| **DDoS attack** | Plataforma lenta o caida | CDN, WAF, escalamiento automatico |
| **Evento deportivo masivo** | Pico 10x trafico normal | Auto-scaling, load testing previo, war room |

---

## 8. Metricas de estabilidad que el PO debe monitorear

| Metrica | Que mide | Target |
|---------|----------|--------|
| **Uptime** | % del tiempo que la plataforma esta operativa | > 99.9% |
| **Error rate** | % de requests que fallan | < 0.1% |
| **Latencia P95** | Tiempo de respuesta del 95% de requests | < 500ms |
| **MTTR** | Mean Time To Recovery (tiempo para recuperarse) | < 30 min |
| **MTTD** | Mean Time To Detect (tiempo para detectar) | < 5 min |
| **Deploy frequency** | Frecuencia de deploys | > 1/semana |
| **Change failure rate** | % de deploys que causan incidentes | < 5% |

---

## 9. War Room: gestion de crisis

### Cuando activar un War Room

- Incidente P1 que afecta a >50% de usuarios
- Evento deportivo masivo con problemas de plataforma
- Brecha de seguridad o fraude masivo
- Regulador requiere accion inmediata

### Roles en el War Room

| Rol | Responsabilidad |
|-----|----------------|
| **Incident Commander** | Coordina la respuesta (generalmente SM o Tech Lead) |
| **Comunicador** | Actualiza a stakeholders y usuarios (PO o Comms) |
| **Investigadores** | Identifican y arreglan el problema (Devs/Ops) |
| **Observador** | Monitorea metricas y confirma resolucion |

---

## 10. Feature Flags: control de lanzamiento

### Como funcionan

```
if (featureFlag.isEnabled("nuevo_flujo_deposito")) {
    // Nuevo flujo de deposito con Yape
    mostrarNuevoFlujo();
} else {
    // Flujo actual
    mostrarFlujoActual();
}
```

### Estrategias de activacion

| Estrategia | Descripcion | Uso |
|-----------|-------------|-----|
| **% de usuarios** | Activar para 5%, 20%, 50%, 100% | Canary release |
| **Por usuario** | Activar para usuarios especificos | Beta testers |
| **Por segmento** | Activar por region, dispositivo, tipo de usuario | Rollout por mercado |
| **Kill switch** | Desactivar inmediatamente sin deploy | Emergencias |
| **Time-based** | Activar/desactivar por fecha | Promociones |

> **Tip entrevista:** "Los feature flags son la herramienta mas poderosa para reducir riesgo en releases. Permiten lanzar codigo a produccion sin activarlo, y revertir en segundos sin deploy. En un negocio de apuestas donde el uptime es critico, esto es esencial."

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| Incidente P1? | Plataforma caida o feature critica rota. Respuesta < 15 min |
| Post-mortem? | Analisis sin culpas: que paso, por que, como evitarlo |
| Cuando rollback? | Error rate > 5%, conversion cae > 20%, > 10 tickets en 1 hora |
| Feature flags? | Codigo desplegado pero controlado. Kill switch para emergencias |
| Bugs vs features? | 70% features, 20% deuda, 10% bugs. Si bugs > 30% hay problema sistemico |
| War room? | Incidente P1 con >50% usuarios afectados. Roles claros, comunicacion constante |
| Go/No-Go? | Checklist: QA ok, rollback plan, monitoring, no hay eventos deportivos criticos |
| MTTR? | Mean Time To Recovery. Target < 30 min |
| Canary release? | 5% → 20% → 50% → 100% gradual con monitoreo |
| Release en apuestas? | NUNCA durante partido importante. Ventanas de bajo trafico |

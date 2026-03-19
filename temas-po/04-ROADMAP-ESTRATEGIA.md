# ROADMAP Y ESTRATEGIA DE PRODUCTO

---

## 1. Que es un Product Roadmap?

El **roadmap** es un plan visual de alto nivel que comunica la **direccion y progreso** del producto a lo largo del tiempo. No es una lista de features con fechas, es una herramienta de alineamiento estrategico.

**Que NO es un roadmap:**
- No es un Gantt chart con fechas fijas
- No es un compromiso de entrega
- No es una lista de tareas

**Que SI es:**
- Una vision de hacia donde va el producto
- Un instrumento de comunicacion con stakeholders
- Un plan vivo que se actualiza periodicamente

---

## 2. Tipos de Roadmap

### Roadmap basado en tiempo (Timeline)

```
Q1 2026              Q2 2026              Q3 2026
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ KYC Digital  │    │ Apuestas en  │    │ App Nativa   │
│ Depositos    │    │ vivo mejorado│    │ iOS/Android  │
│ Yape/Plin    │    │ Casino Live  │    │ Programa VIP │
└──────────────┘    └──────────────┘    └──────────────┘
```

**Ventaja:** Facil de entender para ejecutivos
**Riesgo:** Genera expectativas de fechas fijas

### Roadmap basado en outcomes (Now-Next-Later)

```
NOW (Sprint actual)     NEXT (1-3 meses)        LATER (3-6 meses)
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Deposito Yape   │    │ Apuestas en     │    │ App nativa      │
│ (aumentar       │    │ vivo mejorado   │    │ (reducir        │
│  conversion     │    │ (aumentar       │    │  dependencia    │
│  deposito 15%)  │    │  engagement)    │    │  del browser)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

**Ventaja:** Foco en resultados, no en features
**Recomendado para:** Equipos agiles, POs modernos

> **Tip entrevista:** "Prefiero el roadmap Now-Next-Later porque comunica intencion sin crear compromisos de fechas. Cada item tiene un outcome medible, no solo una feature."

---

## 3. Como defines el Roadmap?

**Proceso del PO para construir un roadmap:**

```
1. VISION         → Donde queremos estar en 1 ano?
2. OBJETIVOS      → Que resultados debemos lograr? (OKRs)
3. DISCOVERY      → Que problemas del usuario resolver? (research)
4. PRIORIZACION   → Que tiene mas impacto? (RICE, stakeholder input)
5. SECUENCIACION  → Que va primero? (dependencias, riesgo, valor)
6. COMUNICACION   → Compartir y alinear con stakeholders
7. ITERACION      → Revisar y ajustar cada trimestre
```

---

## 4. Product Discovery vs Product Delivery

| Aspecto | Discovery | Delivery |
|---------|-----------|----------|
| **Pregunta** | Estamos construyendo lo correcto? | Estamos construyendolo bien? |
| **Foco** | Entender al usuario, validar ideas | Desarrollar, probar, entregar |
| **Actividades** | Entrevistas, prototipos, A/B tests | Sprints, desarrollo, QA, deploy |
| **Output** | Hipotesis validadas, oportunidades priorizadas | Incrementos funcionales |
| **Riesgo que mitiga** | Valor (nadie lo quiere) y usabilidad | Viabilidad y factibilidad tecnica |

**Dual Track Agile:**

```
Discovery Track ──→ Ideas validadas ──→ Backlog refinado
                                           ↓
Delivery Track  ←── Sprint Backlog  ←──────┘
                ──→ Incremento entregado
```

> **Tip entrevista:** "Un error comun es ir directo a construir sin validar. En mi experiencia en banca, vi features que tomaron meses en desarrollar y nadie uso. Ahora hago discovery antes: prototipos en Figma, entrevistas con usuarios, MVPs minimos."

---

## 5. Ciclo de vida del producto

| Fase | Objetivo | Foco del PO |
|------|----------|-------------|
| **Introduccion** | Lanzar y validar product-market fit | MVP, early adopters, iterar rapido |
| **Crecimiento** | Escalar usuarios y revenue | Nuevas features, optimizar conversion, marketing |
| **Madurez** | Maximizar rentabilidad | Retención, eficiencia operativa, diferenciacion |
| **Declive** | Decidir: renovar, pivotar o retirar | Analisis de datos, nuevas oportunidades |

**Ejemplo Apuesta Total:**

```
Fase actual probable: CRECIMIENTO
- Expansion de metodos de pago (Yape, Plin)
- Nuevos tipos de apuesta
- Mejora de UX mobile
- Programa de fidelizacion
- Expansion geografica
```

---

## 6. Lean Startup aplicado al PO

### Build-Measure-Learn

```
        IDEAS
         ↓
    ┌─────────┐
    │  BUILD  │  ← Construir el minimo necesario (MVP)
    └────┬────┘
         ↓
    ┌─────────┐
    │ MEASURE │  ← Medir con datos reales
    └────┬────┘
         ↓
    ┌─────────┐
    │  LEARN  │  ← Aprender y decidir: pivotar o perseverar
    └────┬────┘
         ↓
      NUEVAS IDEAS
```

### MVP (Minimum Viable Product)

El MVP es la **version mas simple** de un producto que permite validar una hipotesis con usuarios reales.

**Ejemplo Apuesta Total:**

```
Hipotesis: "Los usuarios quieren apostar en partidos de Liga 1 peru con cuotas en vivo"

MVP:
- Solo 3 partidos de Liga 1 por jornada
- Solo apuestas 1X2 (ganador/empate)
- Cuotas actualizadas cada 5 minutos (no en real-time)
- Solo depositos con transferencia bancaria

Metricas de validacion:
- % de usuarios que apuestan en vivo vs pre-match
- Monto promedio de apuesta en vivo
- Tasa de retorno (vuelven a apostar en vivo?)
```

---

## 7. Como presentas el roadmap a stakeholders?

**Segun la audiencia:**

| Audiencia | Que quieren ver | Formato |
|-----------|----------------|---------|
| **CEO/Directores** | Vision, ROI, timeline alto nivel | Roadmap de 1 pagina, outcomes |
| **Stakeholders de negocio** | Features que les afectan, fechas estimadas | Now-Next-Later con detalle medio |
| **Equipo de desarrollo** | Items priorizados, dependencias | Backlog en Jira, epicas desglosadas |
| **Soporte/Ops** | Que cambia para los usuarios | Release notes, impacto operativo |

**Template de presentacion:**

```
1. Recordar la Vision del producto (30 seg)
2. Resultados del trimestre anterior (2 min)
3. Objetivos del proximo trimestre - OKRs (3 min)
4. Roadmap Now-Next-Later (5 min)
5. Trade-offs y decisiones tomadas (3 min)
6. Preguntas y feedback (5 min)
```

---

## 8. Que es un Go-to-Market (GTM) plan?

El GTM define **como lanzar** una feature o producto al mercado.

**Componentes:**

| Elemento | Descripcion | Ejemplo |
|----------|-------------|---------|
| **Segmento** | A quien va dirigido | Usuarios activos que depositan con transferencia |
| **Propuesta de valor** | Por que lo usarian | "Deposita en 30 seg con Yape, sin salir de la app" |
| **Canales** | Como se entera el usuario | Push notification, banner in-app, email |
| **Metricas de exito** | Como sabes que funciono | +15% depositos, +10% usuarios activos diarios |
| **Rollout plan** | Como se despliega | 10% → 50% → 100% (canary release) |

---

## 9. Como gestionas dependencias entre equipos?

**Estrategias:**

| Estrategia | Como funciona |
|------------|---------------|
| **Dependency board** | Mapa visual de dependencias entre equipos en Jira |
| **Scrum of Scrums** | Sync semanal entre POs/SMs de equipos relacionados |
| **PI Planning (SAFe)** | Planificacion trimestral conjunta de todos los equipos |
| **Contratos de API** | Definir interfaces antes de desarrollar para desacoplar |
| **Feature flags** | Desplegar codigo sin activarlo hasta que el otro equipo termine |

> **Tip entrevista:** "En Novopayment coordinaba con 3 equipos: pagos, compliance y canales. Usabamos un dependency board en Jira y sync semanal para anticipar bloqueos. La clave es hacer las dependencias visibles temprano."

---

## 10. Que es Product-Market Fit y como lo mides?

**Product-Market Fit (PMF)** es cuando tu producto satisface una necesidad real del mercado de forma que los usuarios lo adoptan y pagan por el.

**Senales de PMF:**

| Senal | Metrica |
|-------|---------|
| Los usuarios vuelven | Retencion D7 > 40% |
| Los usuarios pagan | Conversion free → paid > 5% |
| Los usuarios recomiendan | NPS > 40 |
| El crecimiento es organico | % usuarios por referido > 30% |
| Se quejan cuando no funciona | Tickets de soporte por caidas |

**Pregunta de Sean Ellis:**
> "Como te sentirias si ya no pudieras usar [producto]?"
> - Si >40% dice "Muy decepcionado" → tienes PMF

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| Que es un roadmap? | Plan visual de direccion del producto, no una lista de features con fechas |
| Mejor tipo de roadmap? | Now-Next-Later (basado en outcomes, no en tiempo fijo) |
| Discovery vs Delivery? | Discovery = construimos lo correcto? Delivery = lo construimos bien? |
| Que es MVP? | La version mas simple para validar una hipotesis con usuarios reales |
| Como presentas a CEO? | Vision + resultados + OKRs + roadmap de 1 pagina |
| GTM plan? | Plan de lanzamiento: segmento, propuesta, canales, metricas |
| Dependencias entre equipos? | Dependency board, Scrum of Scrums, contratos de API |
| Product-Market Fit? | Cuando +40% de usuarios dirian que estarian "muy decepcionados" sin tu producto |

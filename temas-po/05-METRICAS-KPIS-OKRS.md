# METRICAS, KPIs Y OKRs

---

## 1. Diferencia entre Metricas, KPIs y OKRs

| Concepto | Que es | Ejemplo |
|----------|--------|---------|
| **Metrica** | Cualquier dato medible del producto | Numero de visitas al sitio |
| **KPI** | Metrica clave que indica salud del negocio | Tasa de conversion registro → deposito |
| **OKR** | Objetivo + Resultados Clave medibles | Obj: Aumentar engagement. KR: DAU +20% |

```
Metrica: "Tenemos 50,000 visitas al mes"          → dato
KPI:     "Nuestra conversion es del 3%"            → indicador de salud
OKR:     "Aumentar conversion al 5% este trimestre" → meta con resultado medible
```

---

## 2. Framework OKR (Objectives and Key Results)

**Estructura:**

```
Objective: [QUE quiero lograr - cualitativo, inspirador]
  KR1: [COMO se que lo logre - cuantitativo, medible]
  KR2: [COMO se que lo logre - cuantitativo, medible]
  KR3: [COMO se que lo logre - cuantitativo, medible]
```

**Reglas de un buen OKR:**
- **Objective**: ambicioso, cualitativo, inspirador, alcanzable en el trimestre
- **Key Results**: medibles, con numero, sin ambiguedad
- **No mas de 3-5 objetivos** por trimestre
- **No mas de 3-4 KRs** por objetivo
- Los KRs NO son tareas, son resultados

**Ejemplo para Apuesta Total:**

```
Objective 1: Ser la plataforma de apuestas con la mejor experiencia de deposito en Peru

  KR1: Aumentar la tasa de conversion de registro a primer deposito del 25% al 40%
  KR2: Reducir el tiempo promedio de deposito de 3 minutos a 45 segundos
  KR3: Alcanzar un NPS de +50 en la encuesta post-deposito

Objective 2: Incrementar el engagement de los usuarios activos

  KR1: Aumentar el DAU (Daily Active Users) de 15K a 22K
  KR2: Incrementar el promedio de apuestas por usuario de 3 a 5 por sesion
  KR3: Reducir el churn mensual de usuarios activos del 12% al 7%

Objective 3: Garantizar una plataforma estable y confiable

  KR1: Mantener uptime del 99.9% en horario de eventos deportivos
  KR2: Reducir el tiempo medio de resolucion de incidentes criticos de 2h a 30min
  KR3: Cero incidentes de seguridad relacionados con datos de pago
```

> **Tip entrevista:** "Los OKRs se miden al 70% como ideal (stretch goals). Si los cumples al 100%, eran demasiado faciles."

---

## 3. North Star Metric

La **North Star Metric** es la unica metrica que mejor captura el valor que tu producto entrega a los usuarios.

| Industria | North Star Metric |
|-----------|------------------|
| Spotify | Tiempo de escucha |
| Airbnb | Noches reservadas |
| WhatsApp | Mensajes enviados |
| Facebook | Daily Active Users |
| **Apuestas** | **Apuestas realizadas por usuario activo por semana** |

**Para Apuesta Total, la North Star podria ser:**

```
"Numero de apuestas realizadas por usuario activo por semana"

Por que? Porque captura:
✓ El usuario esta activo (retention)
✓ El usuario deposita (monetization)
✓ El usuario encuentra eventos para apostar (engagement)
✓ La plataforma funciona bien (experiencia)
```

---

## 4. Metricas AARRR (Pirate Metrics)

| Etapa | Pregunta | Metrica | Ejemplo Apuesta Total |
|-------|----------|---------|----------------------|
| **Acquisition** | Como llegan los usuarios? | Visitas, registros | 10K visitas/mes, 2K registros |
| **Activation** | Tienen una buena primera experiencia? | Primer deposito, primera apuesta | 40% hace primer deposito |
| **Retention** | Vuelven? | DAU/MAU, retencion D7/D30 | 35% vuelve a la semana |
| **Revenue** | Generan ingresos? | GGR, ARPU, LTV | GGR S/500K/mes |
| **Referral** | Recomiendan? | Invitaciones, codigos referido | 15% trae un amigo |

**Funnel de conversion Apuesta Total:**

```
Visita al sitio:        100,000  (100%)
    ↓
Registro:               10,000   (10%)
    ↓
Verificacion KYC:       7,000    (70% de registros)
    ↓
Primer deposito:        3,500    (50% de verificados)
    ↓
Primera apuesta:        3,000    (86% de depositantes)
    ↓
Apuesta recurrente:     1,500    (50% de primera apuesta)
    ↓
Usuario activo mensual: 1,200    (80% de recurrentes)
```

---

## 5. Metricas especificas de Apuestas/Gaming

| Metrica | Definicion | Por que importa |
|---------|-----------|-----------------|
| **GGR** (Gross Gaming Revenue) | Total apostado - Total pagado en premios | Ingreso bruto del negocio |
| **NGR** (Net Gaming Revenue) | GGR - Bonos - Costos operativos | Ingreso neto real |
| **Handle** | Monto total apostado | Volumen de operacion |
| **Hold %** | GGR / Handle x 100 | Margen de la casa (tipico 5-15%) |
| **ARPU** | Revenue / Usuarios activos | Valor promedio por usuario |
| **LTV** | Valor total que genera un usuario en su vida util | Cuanto invertir en adquisicion |
| **CAC** | Costo de adquirir un nuevo usuario | Eficiencia de marketing |
| **Ratio LTV/CAC** | LTV / CAC | Debe ser > 3 para ser sostenible |
| **Churn Rate** | % de usuarios que dejan de apostar por mes | Salud de la retencion |
| **DAU/MAU** | Daily Active / Monthly Active Users | Engagement (ideal > 20%) |
| **Deposito promedio** | Monto promedio por deposito | Ticket medio |
| **Frecuencia de deposito** | Depositos por usuario por mes | Engagement financiero |

---

## 6. Como defines KPIs para una nueva feature?

**Framework HEART (Google):**

| Dimension | Que mide | Metrica ejemplo |
|-----------|----------|-----------------|
| **H**appiness | Satisfaccion del usuario | NPS, CSAT, rating in-app |
| **E**ngagement | Nivel de uso | Frecuencia, duracion, profundidad |
| **A**doption | Nuevos usuarios que usan la feature | % de usuarios que la prueban |
| **R**etention | Usuarios que vuelven | Retencion D1, D7, D30 |
| **T**ask Success | Eficiencia en completar la tarea | Tasa de exito, tiempo, errores |

**Ejemplo: Feature "Deposito con Yape"**

```
Happiness:    NPS post-deposito >= 60
Engagement:   30% de depositos se hacen con Yape (al mes de lanzamiento)
Adoption:     50% de usuarios activos prueban Yape en las primeras 2 semanas
Retention:    80% de quienes usan Yape repiten el metodo
Task Success: 95% de intentos de deposito son exitosos, tiempo < 45 seg
```

---

## 7. Dashboards: que debe monitorear un PO?

**Dashboard diario:**

```
┌─────────────────────────────────────────────────┐
│  DASHBOARD DIARIO - APUESTA TOTAL               │
├──────────────┬──────────────┬───────────────────┤
│ DAU: 18,500  │ Registros: 320│ Depositos: S/45K │
│ (↑ 3% vs ayer)│ (↓ 5%)      │ (↑ 8%)          │
├──────────────┴──────────────┴───────────────────┤
│ Apuestas activas: 12,300  │  GGR hoy: S/28K    │
│ Incidentes abiertos: 2    │  Uptime: 99.95%    │
└─────────────────────────────────────────────────┘
```

**Dashboard semanal/mensual:**

| Seccion | Metricas |
|---------|----------|
| **Adquisicion** | Nuevos registros, CAC, fuente de trafico |
| **Activacion** | Conversion registro → deposito → apuesta |
| **Engagement** | DAU/MAU, apuestas por usuario, sesiones |
| **Revenue** | GGR, NGR, ARPU, LTV |
| **Retencion** | Churn, cohortes, retencion D7/D30 |
| **Operaciones** | Uptime, incidentes, tiempo de respuesta |

---

## 8. A/B Testing para Product Owners

**Que es:** Comparar dos versiones de una feature para ver cual genera mejores resultados.

**Proceso:**

```
1. HIPOTESIS:  "Mostrar cuotas destacadas en el home aumentara las apuestas"
2. VARIANTES:  A (home actual) vs B (home con cuotas destacadas)
3. METRICAS:   Tasa de clic en cuota, apuestas realizadas, GGR
4. SEGMENTO:   50% usuarios aleatorios en cada variante
5. DURACION:   2 semanas minimo (significancia estadistica)
6. DECISION:   Si B > A con 95% confianza → implementar B
```

**Que testear en apuestas:**
- Posicion y diseno del boton "Apostar"
- Orden de deportes/ligas en el home
- Proceso de deposito (pasos, metodos destacados)
- Monto sugerido de apuesta
- Copy de promociones y bonos

> **Tip entrevista:** "Tomo decisiones basadas en datos, no en opiniones. Si hay debate sobre un diseno, propongo un A/B test con metricas claras y dejamos que los usuarios decidan."

---

## 9. Cohort Analysis

Un **analisis de cohortes** agrupa usuarios por cuando se registraron y mide su comportamiento a lo largo del tiempo.

**Ejemplo de tabla de retencion:**

```
Cohorte      Mes 0   Mes 1   Mes 2   Mes 3   Mes 4
Ene 2026     100%    45%     30%     22%     18%
Feb 2026     100%    48%     33%     25%     --
Mar 2026     100%    52%     35%     --      --
Abr 2026     100%    50%     --      --      --
```

**Como interpretarlo:**
- Si la retencion del Mes 1 mejora con el tiempo → las mejoras al onboarding funcionan
- Si hay una caida fuerte en Mes 2 → los usuarios no encuentran suficiente valor despues del inicio
- Compara cohortes antes/despues de un cambio para medir impacto

---

## 10. Como presentas metricas a stakeholders?

**Framework: Dato → Insight → Accion**

```
MALO:  "El DAU fue de 15,000 este mes"
BUENO: "El DAU cayo 10% vs mes anterior (15K vs 16.7K) porque no hubo
        partidos de Liga 1 por dos semanas. Propongo lanzar promociones
        de casino durante ventanas sin futbol para compensar."
```

**Reglas:**
1. **Contexto**: siempre compara (vs mes anterior, vs objetivo, vs benchmark)
2. **Por que**: explica la causa, no solo el numero
3. **Accion**: que vas a hacer al respecto
4. **Visual**: usa graficos, no solo tablas de numeros

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| KPI vs OKR? | KPI = indicador de salud continuo. OKR = meta con resultados medibles para el trimestre |
| Que es North Star? | La unica metrica que mejor captura el valor del producto para el usuario |
| AARRR? | Acquisition, Activation, Retention, Revenue, Referral |
| GGR? | Gross Gaming Revenue = Total apostado - Total pagado en premios |
| LTV/CAC? | LTV / CAC > 3 para ser sostenible |
| A/B test? | Comparar 2 versiones con datos reales para decidir con 95% confianza |
| Cohort analysis? | Agrupar usuarios por fecha de registro y medir retencion en el tiempo |
| Como presentas datos? | Dato + Contexto + Por que + Que vas a hacer |
| OKR al 100% es bueno? | No, ideal es 70%. Al 100% era demasiado facil |
| HEART? | Happiness, Engagement, Adoption, Retention, Task Success |

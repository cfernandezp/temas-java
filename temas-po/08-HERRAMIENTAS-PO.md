# HERRAMIENTAS DEL PRODUCT OWNER

---

## 1. Jira Avanzado

### Estructura de un proyecto Jira para PO

```
PROYECTO: Apuesta Total
├── EPICA: Sistema de Depositos
│   ├── HISTORIA: Deposito con Yape
│   │   ├── SUBTAREA: Crear endpoint /api/deposits/yape
│   │   ├── SUBTAREA: Integrar SDK Yape
│   │   └── SUBTAREA: UI del flujo de deposito
│   ├── HISTORIA: Deposito con tarjeta
│   └── HISTORIA: Deposito con PagoEfectivo
├── EPICA: Sistema de Retiros
│   └── ...
└── EPICA: Apuestas en Vivo
    └── ...
```

### Tipos de issue y cuando usarlos

| Tipo | Uso | Quien lo crea |
|------|-----|--------------|
| **Epic** | Funcionalidad grande (semanas/meses) | PO |
| **Story** | Funcionalidad desde perspectiva del usuario | PO |
| **Task** | Trabajo tecnico no relacionado a una historia | Dev/PO |
| **Bug** | Defecto encontrado en produccion o QA | QA/Soporte |
| **Sub-task** | Division de una historia en partes tecnicas | Dev |
| **Spike** | Investigacion tecnica con timebox | PO/Dev |

### Workflow personalizado tipico

```
BACKLOG → READY (DoR) → IN PROGRESS → IN REVIEW → QA → DONE
                              ↑                       |
                              └── BLOCKED ←───────────┘
```

### JQL (Jira Query Language) - Consultas esenciales

```sql
-- Historias listas para sprint (cumplen DoR)
status = "Ready" AND type = Story AND sprint in openSprints()

-- Bugs criticos sin asignar
type = Bug AND priority = Critical AND assignee = EMPTY

-- Mi backlog priorizado
project = "AT" AND type in (Story, Bug) AND status = "Backlog"
ORDER BY priority DESC, rank ASC

-- Que se entrego en el ultimo sprint
sprint in closedSprints() AND status = Done AND type = Story

-- Items bloqueados hace mas de 2 dias
status = "Blocked" AND status changed TO "Blocked" BEFORE -2d

-- Velocity: story points completados por sprint
type = Story AND status = Done AND sprint in closedSprints()
-- (usar con dashboard/gadget de velocity)

-- Epicas con progreso
type = Epic AND project = "AT"
-- (usar con gadget de Epic Burndown)

-- Items sin estimar
type = Story AND "Story Points" is EMPTY AND sprint in futureSprints()
```

### Dashboards en Jira

**Dashboard del PO debe incluir:**

| Gadget | Para que |
|--------|---------|
| **Sprint Burndown** | Ver progreso diario del sprint |
| **Velocity Chart** | Historico de story points por sprint |
| **Epic Progress** | Avance de cada epica (% stories done) |
| **Created vs Resolved** | Balance de bugs creados vs resueltos |
| **Filter Results** | Items bloqueados, sin asignar, sin estimar |
| **Pie Chart** | Distribucion de items por tipo (story/bug/tech debt) |

### Automatizaciones utiles en Jira

```
REGLA 1: Cuando un bug se crea con prioridad "Critical"
         → Enviar notificacion a Slack al canal #incidents
         → Asignar al PO para triaje

REGLA 2: Cuando todas las sub-tasks de una Story pasan a "Done"
         → Mover la Story a "QA"

REGLA 3: Cuando un item esta en "Blocked" por mas de 48 horas
         → Enviar recordatorio al Scrum Master

REGLA 4: Al cerrar un Sprint
         → Mover items incompletos al siguiente sprint
         → Enviar resumen por email al PO
```

---

## 2. Excel Avanzado para PO

### Funciones esenciales

| Funcion | Uso | Ejemplo |
|---------|-----|---------|
| **BUSCARV / XLOOKUP** | Buscar datos en otra tabla | Buscar nombre de usuario por ID |
| **SI.CONJUNTO** | Multiples condiciones | Categorizar usuarios por nivel de gasto |
| **SUMAR.SI.CONJUNTO** | Sumar con criterios | Total depositado por metodo de pago |
| **CONTAR.SI** | Contar con criterio | Cuantos usuarios depositaron con Yape |
| **TEXTO** | Formatear fechas/numeros | Convertir fecha a "Mar 2026" |
| **INDICE + COINCIDIR** | Busqueda flexible | Buscar en cualquier direccion (mejor que BUSCARV) |

### Tablas Dinamicas

**Caso practico: Analisis de depositos**

```
Datos fuente:
| Fecha | Usuario | Metodo | Monto | Region |
|-------|---------|--------|-------|--------|
| 01/03 | U001    | Yape   | 50    | Lima   |
| 01/03 | U002    | Tarjeta| 100   | Arequipa|
| ...   | ...     | ...    | ...   | ...    |

Tabla Dinamica:
- Filas: Metodo de pago
- Columnas: Mes
- Valores: SUMA de Monto, CUENTA de transacciones
- Filtro: Region

Resultado:
| Metodo      | Ene    | Feb    | Mar    |
|-------------|--------|--------|--------|
| Yape        | S/45K  | S/52K  | S/61K  |
| Tarjeta     | S/30K  | S/28K  | S/25K  |
| PagoEfectivo| S/15K  | S/18K  | S/20K  |
| TOTAL       | S/90K  | S/98K  | S/106K |
```

### Graficos recomendados

| Tipo | Cuando usarlo |
|------|--------------|
| **Linea** | Tendencias en el tiempo (DAU, depositos mensuales) |
| **Barras** | Comparar categorias (depositos por metodo) |
| **Pie/Dona** | Distribucion (% por metodo de pago) |
| **Embudo** | Conversion (registro → deposito → apuesta) |
| **Combo (barras + linea)** | Volumen + tasa (depositos + % conversion) |

### Macros basicas

```vba
' Macro para formatear reporte semanal automaticamente
Sub FormatearReporte()
    ' Seleccionar rango de datos
    Range("A1").CurrentRegion.Select

    ' Aplicar formato de tabla
    ActiveSheet.ListObjects.Add(xlSrcRange, Selection).Name = "TablaReporte"

    ' Formato de moneda a columna de montos
    Columns("D").NumberFormat = "S/ #,##0.00"

    ' Autoajustar columnas
    Cells.EntireColumn.AutoFit
End Sub
```

---

## 3. Power BI para PO

### Conceptos clave

| Concepto | Que es |
|----------|--------|
| **Dataset** | Fuente de datos conectada (SQL, Excel, API) |
| **Report** | Visualizaciones interactivas sobre un dataset |
| **Dashboard** | Coleccion de tiles/graficos de multiples reportes |
| **DAX** | Lenguaje de formulas para calculos (similar a Excel) |
| **Slicers** | Filtros interactivos en el reporte |

### Dashboard PO tipico en Power BI

```
┌─────────────────────────────────────────────────────────┐
│  DASHBOARD PRODUCTO - APUESTA TOTAL                     │
│  [Filtro: Periodo] [Filtro: Region] [Filtro: Plataforma]│
├──────────────┬──────────────┬──────────────┬────────────┤
│ DAU          │ Registros    │ GGR          │ NPS        │
│ 18,500       │ 2,300        │ S/850K       │ 45         │
│ ↑ 5%         │ ↓ 3%         │ ↑ 12%        │ ↑ 3pts     │
├──────────────┴──────────────┴──────────────┴────────────┤
│ [Grafico Linea: DAU ultimos 30 dias]                    │
├─────────────────────────┬───────────────────────────────┤
│ [Embudo: Conversion]    │ [Barras: Depositos x metodo]  │
│ Visitas    100K         │ Yape         45%              │
│ Registros  10K          │ Tarjeta      25%              │
│ Depositos  3.5K         │ PagoEfectivo 20%              │
│ Apuestas   3K           │ Transferencia 10%             │
├─────────────────────────┴───────────────────────────────┤
│ [Tabla: Top 10 eventos con mas apuestas esta semana]    │
└─────────────────────────────────────────────────────────┘
```

### DAX basico

```dax
-- Calcular GGR
GGR = SUM(Apuestas[MontoApostado]) - SUM(Apuestas[MontoPagado])

-- Tasa de conversion
ConversionRate =
    DIVIDE(
        COUNTROWS(FILTER(Usuarios, Usuarios[PrimerDeposito] <> BLANK())),
        COUNTROWS(Usuarios),
        0
    )

-- DAU (usuarios unicos por dia)
DAU = DISTINCTCOUNT(Sesiones[UserID])

-- Comparacion vs mes anterior
GGR_vs_MesAnterior =
    VAR GGR_Actual = [GGR]
    VAR GGR_Anterior = CALCULATE([GGR], DATEADD(Calendario[Fecha], -1, MONTH))
    RETURN DIVIDE(GGR_Actual - GGR_Anterior, GGR_Anterior, 0)
```

---

## 4. Confluence / Notion para documentacion

### Paginas esenciales del PO

| Pagina | Contenido |
|--------|-----------|
| **Vision del Producto** | Vision, mision, propuesta de valor |
| **Roadmap** | Now-Next-Later actualizado |
| **OKRs del trimestre** | Objetivos y resultados clave con status |
| **PRD (Product Requirements Doc)** | Especificacion detallada por feature |
| **Decision Log** | Registro de decisiones tomadas y su razon |
| **Meeting Notes** | Notas de refinamiento, planning, reviews |
| **Metricas y Dashboards** | Links a Power BI, Grafana, Analytics |
| **Glossary** | Terminos del dominio (apuestas, pagos, compliance) |

### Template de PRD (Product Requirements Document)

```
# PRD: [Nombre de la Feature]

## Contexto
Por que estamos haciendo esto? Que problema resuelve?

## Objetivo
Que metrica queremos mover? Cuanto?

## Usuarios objetivo
Para quien es? Segmento especifico.

## Historias de usuario
- Como [usuario]...

## Criterios de aceptacion
- DADO... CUANDO... ENTONCES...

## Diseno
Link al Figma: [URL]

## Fuera de alcance
Que NO incluye esta version.

## Dependencias
Que necesitamos de otros equipos?

## Metricas de exito
Como medimos si funciono?

## Timeline estimado
- Discovery: Semana 1
- Desarrollo: Semanas 2-4
- QA: Semana 5
- Launch: Semana 6
```

---

## 5. Herramientas de comunicacion y colaboracion

| Herramienta | Uso del PO |
|-------------|-----------|
| **Slack/Teams** | Comunicacion diaria, canales por tema, alertas |
| **Miro/FigJam** | Workshops remotos, brainstorming, journey maps |
| **Loom** | Grabar demos, explicar contexto asincrono |
| **Google Analytics** | Analisis de trafico y comportamiento web |
| **Hotjar/FullStory** | Heatmaps, grabaciones de sesion, encuestas |
| **Mixpanel/Amplitude** | Product analytics, funnels, cohortes |

---

## 6. Gestion del tiempo del PO

**Distribucion tipica del tiempo semanal:**

```
| Actividad                    | Tiempo  |
|------------------------------|---------|
| Refinamiento del backlog     | 20%     |
| Reuniones con stakeholders   | 20%     |
| Ceremonias Scrum             | 15%     |
| Analisis de datos/metricas   | 15%     |
| Discovery/Research           | 15%     |
| Documentacion                | 10%     |
| Desarrollo personal          | 5%      |
```

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| JQL basico? | `status = "Blocked" AND sprint in openSprints()` |
| Tabla dinamica? | Filas = dimension, Valores = medida, Filtros = segmento |
| Dashboard PO en Jira? | Burndown, velocity, epic progress, bugs created vs resolved |
| Power BI vs Excel? | Excel para analisis ad-hoc, Power BI para dashboards en vivo |
| DAX? | Lenguaje de formulas de Power BI. Similar a Excel pero para modelos de datos |
| PRD? | Documento de especificacion: contexto, objetivo, historias, metricas de exito |
| Que automatizas en Jira? | Notificaciones de bugs criticos, mover stories a QA, alertas de bloqueo |
| Confluence vs Notion? | Ambos sirven. Confluence se integra mejor con Jira. Notion es mas flexible |

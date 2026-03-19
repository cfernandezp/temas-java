# STAKEHOLDER MANAGEMENT

---

## 1. Que es un Stakeholder?

Un **stakeholder** es cualquier persona o grupo que tiene interes o influencia en el producto. Como PO, debes identificarlos, entender sus necesidades y gestionarlos activamente.

**Stakeholders tipicos en Apuesta Total:**

| Stakeholder | Que le importa | Frecuencia de comunicacion |
|-------------|---------------|---------------------------|
| **CEO/Gerente General** | Revenue, crecimiento, vision | Mensual/trimestral |
| **Gerente Comercial** | Promociones, conversion, GGR | Semanal |
| **Marketing** | Adquisicion, campanas, UX del registro | Semanal |
| **Compliance/Legal** | Regulacion MINCETUR, KYC, LAFT | Cuando hay cambios regulatorios |
| **Soporte/Call Center** | Reducir tickets, facilitar autoservicio | Bisemanal |
| **Finanzas** | Costos, margen, forecast | Mensual |
| **Operaciones/Riesgo** | Fraude, limites, estabilidad | Semanal |
| **Equipo de desarrollo** | Claridad del backlog, prioridades | Diario |
| **Usuarios finales** | Experiencia, velocidad, confianza | Continuo (data, research) |

---

## 2. Mapa de Stakeholders (Power/Interest Grid)

```
              Alto Interes
                   |
    MANTENER       |    GESTIONAR DE CERCA
    SATISFECHO     |    (Key Players)
    Gerente        |    CEO, Gerente Comercial
    Finanzas       |    Marketing
                   |
  ─────────────────┼─────────────────
                   |
    MONITOREAR     |    MANTENER INFORMADO
    (Minimo        |    Soporte, Legal
     esfuerzo)     |    Operaciones
    Proveedor      |
    externo        |
                   |
              Bajo Interes

  Bajo Poder  ←────────→  Alto Poder
```

**Estrategia por cuadrante:**

| Cuadrante | Estrategia | Ejemplo |
|-----------|-----------|---------|
| **Alto poder + Alto interes** | Gestionar de cerca, involucrar en decisiones | CEO: reportes mensuales, alineamiento de vision |
| **Alto poder + Bajo interes** | Mantener satisfecho, no abrumar | Finanzas: resumen ejecutivo mensual |
| **Bajo poder + Alto interes** | Mantener informado, escuchar feedback | Soporte: compartir release notes, recoger pain points |
| **Bajo poder + Bajo interes** | Monitorear, comunicar lo minimo | Proveedores: solo cuando hay impacto |

---

## 3. Como manejas conflictos de prioridad entre stakeholders?

**Escenario tipico:**
> Marketing quiere una campana de bonos para el fin de semana.
> Operaciones quiere arreglar el bug de retiros primero.
> El CEO quiere el nuevo modulo de casino live.

**Framework de resolucion:**

```
1. DATOS:        Cual tiene mayor impacto en la North Star Metric?
2. URGENCIA:     Hay un deadline externo real? (regulacion, evento deportivo)
3. COSTO:        Que pasa si NO lo hacemos ahora?
4. DEPENDENCIA:  Uno bloquea al otro?
5. TRANSPARENCIA: Mostrar el trade-off a todos los stakeholders
6. DECISION:     El PO decide con base en datos, no en quien grita mas fuerte
```

**Ejemplo de respuesta:**

```
"Marketing, entiendo la urgencia de la campana. Sin embargo, los retiros
fallidos estan generando 50 tickets diarios y podemos perder la confianza
de usuarios que ya depositan. Propongo: el equipo arregla el bug de retiros
esta semana (3 dias) y la campana de bonos la lanzamos el jueves con la
plataforma estabilizada. Asi la campana no trae usuarios a una experiencia
rota."
```

> **Tip entrevista:** "No priorizo por quien tiene mas poder politico. Priorizo por impacto en el usuario y el negocio, y lo comunico con datos para que todos entiendan el por que."

---

## 4. Como dices NO con fundamento?

**El framework del "Si, pero...":**

| Situacion | Mala respuesta | Buena respuesta |
|-----------|---------------|-----------------|
| Stakeholder pide feature urgente | "No, estamos ocupados" | "Entiendo el valor. Si la incluimos, sacamos X del sprint. Prefieres ese trade-off?" |
| CEO quiere algo para manana | "Es imposible" | "Puedo entregar un MVP minimo en 3 dias. La version completa necesita 2 sprints. Cual prefieres?" |
| Todos quieren ser prioridad 1 | "No puedo hacer todo" | "Aqui estan las 5 iniciativas con su impacto estimado. Solo caben 3 este trimestre. Ayudenme a elegir" |

**Tecnicas:**
- **Mostrar el backlog**: "Mira, esto es todo lo que esta en cola. Donde lo ponemos?"
- **Cuantificar el costo**: "Si hacemos X, retrasamos Y en 3 semanas. Y genera S/100K/mes"
- **Ofrecer alternativas**: "No puedo la version completa, pero puedo un MVP en la mitad del tiempo"
- **Pedir criterios**: "Que metrica usariamos para saber si esto fue exitoso?"

---

## 5. Comunicacion efectiva segun audiencia

### Para ejecutivos (CEO, directores)
- **Formato**: resumen ejecutivo de 1 pagina, 5 minutos max
- **Contenido**: resultados, ROI, riesgos, decisiones necesarias
- **Evitar**: detalles tecnicos, jerga de Scrum

```
Ejemplo:
"Este trimestre lanzamos depositos con Yape. Resultado: +23% en depositos
totales, +15% en usuarios activos. Proximo paso: apuestas en vivo mejoradas,
esperamos +10% en GGR. Necesito aprobacion para contratar un dev mas."
```

### Para negocio (marketing, comercial)
- **Formato**: reunion semanal, 30 minutos
- **Contenido**: que se lanzo, que viene, como les impacta
- **Clave**: hablar en terminos de campanas, conversion, usuarios

### Para tecnologia (equipo de desarrollo)
- **Formato**: refinamiento, planning, dailies
- **Contenido**: historias detalladas, criterios de aceptacion, prioridades
- **Clave**: ser claro en el QUE, dejar el COMO al equipo

---

## 6. Tecnicas de facilitacion para el PO

### Facilitacion de reuniones

| Tecnica | Cuando usarla | Como funciona |
|---------|--------------|---------------|
| **Dot voting** | Priorizar ideas en grupo | Cada persona tiene 3 votos, pone puntos en las ideas |
| **Roman voting** | Decision rapida del grupo | Pulgar arriba/abajo/medio simultaneo |
| **1-2-4-All** | Generar ideas inclusivamente | 1 min solo → 2 min en pares → 4 min en grupo → compartir |
| **Silent brainstorm** | Evitar que los mas vocales dominen | Escribir ideas en post-its antes de discutir |
| **5 Whys** | Encontrar la causa raiz | Preguntar "por que?" 5 veces hasta llegar a la raiz |
| **Impact mapping** | Conectar features con objetivos | Goal → Actors → Impacts → Deliverables |

---

## 7. Como manejas expectativas?

**Regla de oro:** Under-promise, over-deliver.

**Practicas:**

| Practica | Ejemplo |
|----------|---------|
| **Dar rangos, no fechas exactas** | "Entre la semana 3 y 4 de abril" en vez de "el 20 de abril" |
| **Compartir riesgos temprano** | "Dependemos del proveedor de pagos, si se atrasa nos impacta" |
| **Actualizar proactivamente** | No esperar a que pregunten, enviar updates semanales |
| **Hacer demo temprana** | Mostrar progreso en la Review, no solo al final |
| **Documentar acuerdos** | Enviar email post-reunion con lo decidido y los next steps |

---

## 8. Stakeholder anti-patterns y como manejarlos

| Anti-pattern | Comportamiento | Como manejarlo |
|-------------|---------------|----------------|
| **El HiPPO** | Highest Paid Person's Opinion domina | Traer datos y A/B tests para objetivizar |
| **El micromanager** | Quiere controlar cada detalle | Establecer checkpoints claros y darle visibilidad |
| **El ausente** | Nunca disponible para validar | Agendar slots fijos, usar comunicacion asincrona |
| **El scope creeper** | Siempre quiere "una cosita mas" | Mostrar el impacto en el sprint, pedir trade-off |
| **El pesimista** | "Esto no va a funcionar" | Invitarlo al discovery, hacerlo parte de la solucion |

---

## 9. Reportes y comunicacion del PO

### Status report semanal

```
REPORTE SEMANAL - [Producto] - Semana [X]

ESTADO GENERAL: 🟢 En track / 🟡 En riesgo / 🔴 Bloqueado

LOGROS DE LA SEMANA:
- [Feature/fix entregado] → [impacto medido]
- [Feature/fix entregado] → [impacto medido]

EN PROGRESO:
- [Feature] → [% avance] → [ETA]

RIESGOS/BLOQUEOS:
- [Riesgo] → [Accion tomada/requerida]

PLAN PROXIMA SEMANA:
- [Que se va a trabajar]

DECISIONES NECESARIAS:
- [Pregunta para stakeholders]
```

---

## 10. Como construyes confianza con tu equipo de desarrollo?

| Practica | Por que funciona |
|----------|-----------------|
| **Respetar el Sprint Backlog** | No meter cambios a mitad de sprint = predecibilidad |
| **Llegar preparado al refinamiento** | Historias claras = menos retrabajo |
| **Proteger al equipo** | No dejar que stakeholders presionen directamente al dev team |
| **Reconocer el trabajo** | Dar credito en la Review, celebrar logros |
| **Ser transparente** | Compartir el contexto de negocio, no solo las tareas |
| **Confiar en el COMO** | Definir el QUE, dejar que el equipo decida la solucion tecnica |

> **Tip entrevista:** "Mi background tecnico me ayuda a no pedir imposibles y a ganarnos la confianza del equipo. Cuando un dev dice 'esto es complejo', entiendo por que y busco alternativas en vez de presionar."

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| Que es un stakeholder? | Cualquier persona con interes o influencia en el producto |
| Como mapeas stakeholders? | Power/Interest Grid: alto poder + alto interes = gestionar de cerca |
| Conflicto de prioridades? | Datos + impacto en North Star + costo de oportunidad + transparencia |
| Como dices NO? | "Si, pero con este trade-off..." + datos + alternativas |
| Como hablas con CEO? | Resumen 1 pagina: resultados, ROI, riesgos, decisiones |
| HiPPO? | Contrarrestar opiniones con datos y A/B tests |
| Scope creep? | Mostrar impacto: "si metemos X, sacamos Y" |
| Confianza con devs? | Respetar sprint, llegar preparado, proteger al equipo |

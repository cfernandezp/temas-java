# SCRUM Y METODOLOGIAS AGILES

---

## 1. Que es Scrum?

**Scrum** es un framework agil para desarrollar productos complejos de forma iterativa e incremental. Se basa en **empirismo**: transparencia, inspeccion y adaptacion.

**Los 3 pilares:**

| Pilar | Que significa | Ejemplo practico |
|-------|--------------|-----------------|
| **Transparencia** | Todos ven el estado real del trabajo | Backlog visible, burndown actualizado |
| **Inspeccion** | Revisar frecuentemente el progreso | Sprint Review, Daily Standup |
| **Adaptacion** | Ajustar el plan basado en lo aprendido | Cambiar prioridades tras feedback del usuario |

**Los 5 valores de Scrum:**
- **Compromiso**: el equipo se compromete con los objetivos del sprint
- **Foco**: concentrarse en el trabajo del sprint
- **Apertura**: transparencia sobre el progreso y problemas
- **Respeto**: respetar la capacidad y expertise de cada miembro
- **Coraje**: hacer lo correcto aunque sea dificil

---

## 2. Roles en Scrum

| Rol | Responsabilidad | NO es |
|-----|----------------|-------|
| **Product Owner** | Maximizar valor, gestionar backlog | Un tomador de pedidos |
| **Scrum Master** | Facilitar el proceso, remover impedimentos | Un jefe de proyecto |
| **Developers** | Construir el incremento del producto | Recursos asignados a tareas |

> **Tip entrevista:** "El PO decide QUE construir, el equipo decide COMO construirlo. El Scrum Master se asegura de que el proceso funcione."

---

## 3. Eventos de Scrum (Ceremonias)

### Sprint
- **Duracion**: 1-4 semanas (mas comun: 2 semanas)
- **Regla**: el Sprint Goal no cambia una vez iniciado
- **Output**: un Incremento potencialmente entregable

### Sprint Planning
- **Duracion**: max 8 horas para sprint de 4 semanas (4h para 2 semanas)
- **Participantes**: PO + Developers + SM
- **Output**: Sprint Goal + Sprint Backlog

**Como PO, tu rol en el Planning:**
1. Presentar los items priorizados del backlog
2. Explicar el Sprint Goal propuesto
3. Responder dudas del equipo sobre los items
4. Negociar el alcance si el equipo ve que no cabe

```
Ejemplo de Sprint Goal:
"Permitir al usuario hacer su primer deposito usando Yape,
para aumentar la conversion de registro a primer deposito en un 15%"
```

### Daily Standup
- **Duracion**: max 15 minutos
- **Participantes**: Developers (PO puede asistir pero no es obligatorio)
- **Foco**: que hice ayer, que hare hoy, tengo impedimentos?

> **Tip entrevista:** "Como PO asisto al Daily para estar informado, pero no dirijo ni asigno tareas. Si surge algo que requiere re-priorizar, lo discuto con el equipo despues del Daily."

### Sprint Review
- **Duracion**: max 4 horas (sprint de 4 semanas)
- **Participantes**: Scrum Team + Stakeholders
- **Foco**: demostrar el incremento, recibir feedback, adaptar el backlog

**Como PO en la Review:**
1. Mostrar que se logro vs el Sprint Goal
2. Demo del incremento funcionando
3. Recoger feedback de stakeholders
4. Actualizar el backlog segun lo aprendido

### Sprint Retrospectiva
- **Duracion**: max 3 horas (sprint de 4 semanas)
- **Participantes**: Scrum Team (sin stakeholders)
- **Foco**: que salio bien, que mejorar, que acciones tomar

---

## 4. Artefactos de Scrum

| Artefacto | Descripcion | Compromiso asociado |
|-----------|-------------|-------------------|
| **Product Backlog** | Lista priorizada de todo lo que el producto necesita | Product Goal |
| **Sprint Backlog** | Items seleccionados para el sprint + plan | Sprint Goal |
| **Incremento** | Suma de todos los items completados (Done) | Definition of Done |

---

## 5. Definition of Ready (DoR) vs Definition of Done (DoD)

### Definition of Ready (DoR)
Criterios que un item del backlog debe cumplir ANTES de entrar a un sprint:

```
Checklist DoR tipico:
[ ] Historia de usuario escrita con criterios de aceptacion
[ ] Mockup/wireframe aprobado (si aplica)
[ ] Dependencias identificadas y resueltas
[ ] El equipo entiende la historia (ya fue refinada)
[ ] Es estimable y cabe en un sprint
[ ] Datos de prueba identificados
```

### Definition of Done (DoD)
Criterios que un item debe cumplir para considerarse TERMINADO:

```
Checklist DoD tipico:
[ ] Codigo desarrollado y revisado (code review)
[ ] Tests unitarios escritos y pasando
[ ] Tests de integracion pasando
[ ] Desplegado en ambiente de QA
[ ] Pruebas funcionales aprobadas por QA
[ ] Documentacion actualizada (si aplica)
[ ] Sin bugs criticos abiertos
[ ] Demo preparada para Review
```

> **Tip entrevista:** "La DoR es responsabilidad del PO (que los items lleguen listos al sprint). La DoD es responsabilidad del equipo (que lo entregado cumpla estandares de calidad)."

---

## 6. Kanban vs Scrum

| Aspecto | Scrum | Kanban |
|---------|-------|--------|
| **Cadencia** | Sprints fijos (1-4 semanas) | Flujo continuo |
| **Roles** | PO, SM, Developers | No define roles |
| **Cambios** | No se cambia el Sprint Backlog | Se pueden agregar items en cualquier momento |
| **Metricas** | Velocity, burndown | Lead time, cycle time, throughput |
| **WIP limits** | Implicito (Sprint Backlog) | Explicito por columna |
| **Mejor para** | Desarrollo de producto con entregas regulares | Soporte, mantenimiento, ops |

> **Tip entrevista:** "En mi experiencia, un hibrido funciona bien: Scrum para features nuevas y Kanban para bugs y soporte. En apuestas, donde hay eventos en vivo y urgencias frecuentes, tener un flujo Kanban para incidentes es clave."

---

## 7. Metricas Agiles que el PO debe conocer

| Metrica | Que mide | Como se usa |
|---------|----------|-------------|
| **Velocity** | Story points completados por sprint | Planificacion de capacidad |
| **Burndown Chart** | Trabajo restante en el sprint | Visualizar progreso diario |
| **Burnup Chart** | Trabajo completado vs alcance total | Ver si el scope crece |
| **Lead Time** | Tiempo desde que se pide hasta que se entrega | Prediccion de entregas |
| **Cycle Time** | Tiempo desde que se empieza hasta que se termina | Eficiencia del equipo |
| **Throughput** | Items completados por unidad de tiempo | Capacidad real |
| **Cumulative Flow** | Visualiza items en cada estado | Detectar cuellos de botella |

---

## 8. Que es un Sprint Goal y por que es importante?

El Sprint Goal es el **objetivo unico** del sprint que da coherencia a los items seleccionados.

**Caracteristicas de un buen Sprint Goal:**
- Es un objetivo de negocio, no una lista de tareas
- Es alcanzable dentro del sprint
- Permite flexibilidad en los items (se puede negociar alcance manteniendo el goal)
- Todo el equipo lo entiende y se compromete

**Ejemplos buenos vs malos:**

| Malo (lista de tareas) | Bueno (objetivo de negocio) |
|------------------------|----------------------------|
| "Completar las historias US-101, US-102, US-103" | "Permitir que el usuario se registre y haga su primer deposito" |
| "Hacer los 5 items del backlog" | "Reducir el tiempo de carga de la pagina de apuestas en vivo a menos de 2 segundos" |
| "Corregir los bugs del sprint anterior" | "Estabilizar el flujo de retiros para eliminar reclamos del call center" |

---

## 9. Como manejas un sprint que va mal?

**Senales de alerta:**
- Burndown plano (no baja)
- Impedimentos no resueltos por mas de 2 dias
- Scope creep (se agregan cosas)
- El equipo no va a cumplir el Sprint Goal

**Que hacer como PO:**
1. **No entrar en panico**: el sprint es un timebox de aprendizaje
2. **Hablar con el equipo**: entender las causas reales
3. **Negociar alcance**: quitar items menos prioritarios manteniendo el Sprint Goal
4. **NO cancelar el sprint** salvo que el Sprint Goal ya no tenga sentido
5. **Documentar aprendizajes** para la retro

> **Tip entrevista:** "En MiBanco tuve un sprint donde una integracion con el core fallo. En vez de cancelar, renegociamos alcance: entregamos el flujo con mock data y movimos la integracion real al siguiente sprint. El stakeholder vio avance y el equipo no se bloqueo."

---

## 10. Scaled Agile: cuando hay multiples equipos

| Framework | Descripcion | Cuando usarlo |
|-----------|-------------|---------------|
| **SAFe** | Framework empresarial con trenes de release (ART) | Grandes organizaciones (+50 personas) |
| **LeSS** | Scrum escalado con un solo backlog para multiples equipos | 2-8 equipos |
| **Nexus** | Extension de Scrum para 3-9 equipos | Equipos trabajando en el mismo producto |
| **Spotify Model** | Squads, Tribes, Chapters, Guilds | Organizaciones con cultura autonoma |

> **Tip entrevista:** No necesitas ser experto, pero menciona: "Conozco SAFe y LeSS conceptualmente. Mi enfoque ha sido coordinar con otros equipos a traves de Scrum of Scrums y alineamiento de dependencias en el refinamiento."

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| 3 pilares de Scrum? | Transparencia, inspeccion, adaptacion |
| Rol del PO en Planning? | Presentar items priorizados, explicar el Goal, responder dudas |
| DoR vs DoD? | DoR = listo para entrar al sprint. DoD = criterios de terminado |
| Scrum vs Kanban? | Scrum = sprints fijos, Kanban = flujo continuo con WIP limits |
| Velocity sirve para? | Planificar capacidad, NO para comparar equipos |
| Sprint va mal? | Negociar alcance, no cancelar, aprender en retro |
| Sprint Goal bueno? | Objetivo de negocio, no lista de tareas |
| Que es un incremento? | Producto funcional y potencialmente entregable al final del sprint |

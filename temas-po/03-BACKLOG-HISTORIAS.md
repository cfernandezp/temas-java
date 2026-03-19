# GESTION DEL BACKLOG E HISTORIAS DE USUARIO

---

## 1. Que es el Product Backlog?

El **Product Backlog** es una lista **ordenada** de todo lo que se necesita en el producto. Es la unica fuente de trabajo para el equipo Scrum.

**Caracteristicas (DEEP):**

| Caracteristica | Significado |
|---------------|-------------|
| **D**etailed appropriately | Items de arriba estan mas detallados que los de abajo |
| **E**stimated | Todos los items tienen estimacion |
| **E**mergent | El backlog cambia y evoluciona constantemente |
| **P**rioritized | Ordenado por valor, riesgo, dependencia |

**Anatomia del backlog:**

```
[ARRIBA - Sprint actual/proximo]
┌─────────────────────────────────┐
│ Historias pequenas, detalladas, │  ← Listas para sprint (DoR)
│ con criterios de aceptacion     │
│ y estimacion                    │
├─────────────────────────────────┤
│ Historias medianas, necesitan   │  ← Para refinar en 1-2 sprints
│ refinamiento                    │
├─────────────────────────────────┤
│ Epicas grandes, poco detalle    │  ← Futuro, ideas a explorar
│ Necesitan descomposicion        │
└─────────────────────────────────┘
[ABAJO - Futuro lejano]
```

---

## 2. Tipos de items en el backlog (PBIs)

| Tipo | Descripcion | Tamano | Ejemplo |
|------|-------------|--------|---------|
| **Epica** | Funcionalidad grande que se descompone en historias | Semanas/meses | "Sistema de retiros" |
| **Historia de Usuario** | Funcionalidad desde la perspectiva del usuario | 1-5 dias | "Como usuario, quiero retirar con Yape" |
| **Tarea** | Trabajo tecnico dentro de una historia | Horas | "Crear endpoint /api/withdrawals" |
| **Bug** | Defecto a corregir | Variable | "El saldo no se actualiza tras deposito" |
| **Spike** | Investigacion tecnica con timebox | 1-2 dias | "Evaluar integracion con pasarela X" |
| **Deuda Tecnica** | Mejora interna sin valor visible al usuario | Variable | "Migrar servicio de pagos a nueva version" |

---

## 3. Como escribir una Historia de Usuario

**Formato clasico:**

```
Como [tipo de usuario]
Quiero [accion/funcionalidad]
Para [beneficio/valor]
```

**Ejemplo Apuesta Total:**

```
Como apostador registrado
Quiero depositar dinero usando Yape
Para poder realizar apuestas rapidamente sin salir de la app
```

**Criterios de aceptacion (Given-When-Then / Dado-Cuando-Entonces):**

```
Criterios de Aceptacion:

DADO que soy un usuario verificado con cuenta activa
CUANDO selecciono "Depositar con Yape" e ingreso un monto de S/50
ENTONCES se genera un QR/enlace de pago Yape
  Y al confirmar el pago, mi saldo se actualiza en menos de 30 segundos
  Y recibo una notificacion push confirmando el deposito
  Y se registra la transaccion en mi historial

DADO que ingreso un monto menor al minimo (S/10)
CUANDO presiono "Depositar"
ENTONCES veo un mensaje de error: "El monto minimo de deposito es S/10"

DADO que el servicio de Yape no esta disponible
CUANDO intento depositar
ENTONCES veo un mensaje: "Yape no disponible, intenta otro metodo de pago"
  Y se muestran metodos alternativos
```

---

## 4. Criterios INVEST para buenas historias

| Criterio | Significado | Pregunta de validacion |
|----------|-------------|----------------------|
| **I**ndependent | No depende de otra historia | Puedo entregarla sola? |
| **N**egotiable | No es un contrato, es una conversacion | Hay espacio para discutir el como? |
| **V**aluable | Aporta valor al usuario o negocio | El usuario notaria si no la hacemos? |
| **E**stimable | El equipo puede estimar su esfuerzo | El equipo entiende que se pide? |
| **S**mall | Cabe en un sprint | Se puede terminar en 1-5 dias? |
| **T**estable | Se puede verificar objetivamente | Los criterios de aceptacion son claros? |

---

## 5. Tecnicas de Priorizacion

### MoSCoW

| Categoria | Significado | % del backlog |
|-----------|-------------|---------------|
| **Must have** | Sin esto el producto no funciona | ~60% |
| **Should have** | Importante pero no bloqueante | ~20% |
| **Could have** | Deseable si hay tiempo | ~20% |
| **Won't have** | No ahora, quizas en el futuro | Fuera del scope |

### RICE Score

```
RICE = (Reach x Impact x Confidence) / Effort

Reach:      cuantos usuarios impacta (numero)
Impact:     nivel de impacto (3=masivo, 2=alto, 1=medio, 0.5=bajo, 0.25=minimo)
Confidence: que tan seguro estamos (100%, 80%, 50%)
Effort:     esfuerzo en persona-sprints
```

**Ejemplo:**

| Feature | Reach | Impact | Confidence | Effort | RICE |
|---------|-------|--------|------------|--------|------|
| Deposito con Yape | 5000 | 3 | 90% | 2 | 6750 |
| Nuevo diseno de lobby | 8000 | 1 | 60% | 4 | 1200 |
| Push de cuota favorita | 3000 | 2 | 70% | 1 | 4200 |

### Value vs Effort Matrix

```
              Alto Valor
                  |
    HACER YA      |    PLANIFICAR
    (Quick Wins)  |    (Big Bets)
                  |
  ────────────────┼────────────────
                  |
    LLENAR        |    NO HACER
    (si hay       |    (descartable)
     tiempo)      |
                  |
              Bajo Valor

  Bajo Esfuerzo ←──→ Alto Esfuerzo
```

---

## 6. Refinamiento del Backlog (Grooming)

El **refinamiento** es la actividad de preparar los items del backlog para sprints futuros.

**Que se hace:**
- Descomponer epicas en historias
- Agregar/mejorar criterios de aceptacion
- Estimar historias (Planning Poker, T-Shirt sizing)
- Identificar dependencias y riesgos
- Reordenar prioridades

**Reglas practicas:**
- Dedicar **~10% del tiempo del sprint** al refinamiento
- Refinar items para los proximos **2-3 sprints**
- El PO prepara los items ANTES de la sesion
- Todo el equipo participa (no solo el PO)

**Ejemplo de agenda de refinamiento (1 hora):**

```
1. [10 min] PO presenta 3-4 historias nuevas
2. [30 min] Equipo pregunta, discute, sugiere alternativas
3. [15 min] Estimacion (Planning Poker)
4. [5 min]  Resumen: que queda listo, que necesita mas trabajo
```

---

## 7. Estimacion: Story Points vs Horas

| Aspecto | Story Points | Horas |
|---------|-------------|-------|
| **Que mide** | Complejidad relativa | Tiempo absoluto |
| **Referencia** | Comparacion con historias anteriores | Experiencia del desarrollador |
| **Precision** | Baja (y esta bien) | Falsa precision |
| **Escala** | Fibonacci: 1, 2, 3, 5, 8, 13, 21 | Horas exactas |
| **Ventaja** | Desacopla estimacion de quien lo hace | Facil de entender para stakeholders |

**Planning Poker:**

```
1. PO lee la historia
2. Equipo discute brevemente
3. Cada miembro muestra su carta (Fibonacci)
4. Si hay diferencia grande: los extremos explican
5. Se vuelve a votar
6. Se registra el consenso
```

> **Tip entrevista:** "Como PO, NO estimo. Mi rol es asegurar que las historias sean claras para que el equipo pueda estimar bien. Si las estimaciones son muy altas, negocio alcance, no presiono al equipo."

---

## 8. Como manejas la Deuda Tecnica?

La deuda tecnica es trabajo tecnico que no aporta valor visible al usuario pero es necesario para la salud del producto.

**Estrategia del PO:**

| Enfoque | Como funciona |
|---------|---------------|
| **Regla del 20%** | Reservar 20% de la capacidad del sprint para deuda tecnica |
| **Hacerla visible** | Incluirla como items en el backlog, no ocultarla |
| **Traducirla a negocio** | "Si no migramos el servicio, los retiros fallaran en Black Friday" |
| **Negociar con datos** | Mostrar metricas de incidentes, tiempos de deploy, bugs recurrentes |

> **Tip entrevista:** "La deuda tecnica es como la deuda financiera: ignorarla no la hace desaparecer, la hace mas cara. Mi rol como PO es hacerla visible al negocio y negociar espacio para pagarla."

---

## 9. Como descompones una Epica en Historias?

**Tecnicas de splitting:**

| Tecnica | Descripcion | Ejemplo |
|---------|-------------|---------|
| **Por flujo** | Dividir por pasos del proceso | Registro → Verificacion → Deposito → Apuesta |
| **Por regla de negocio** | Una historia por regla | Deposito con Yape / Deposito con tarjeta / Deposito con PagoEfectivo |
| **Por tipo de dato** | Diferentes inputs/outputs | Buscar por equipo / Buscar por liga / Buscar por fecha |
| **Por operacion CRUD** | Create, Read, Update, Delete | Crear apuesta / Ver mis apuestas / Cancelar apuesta |
| **Por rol** | Diferentes tipos de usuario | Apuesta como usuario nuevo / Apuesta como usuario VIP |
| **Happy path + excepciones** | Primero el flujo ideal, luego los errores | Deposito exitoso / Deposito rechazado / Deposito timeout |

**Ejemplo de splitting (Epica: Sistema de Retiros):**

```
Epica: "Como usuario, quiero retirar mis ganancias"

Historias:
1. Retiro con transferencia bancaria (happy path)
2. Retiro con Yape
3. Validacion de monto minimo/maximo de retiro
4. Restriccion: no retirar bono sin cumplir rollover
5. Notificacion de retiro procesado
6. Historial de retiros
7. Cancelar retiro pendiente
```

---

## 10. Como dices NO a un stakeholder que quiere meter algo al sprint?

**Framework para decir NO con fundamento:**

```
1. ESCUCHAR:   "Entiendo que necesitas X y por que es importante"
2. CONTEXTO:   "Actualmente estamos comprometidos con [Sprint Goal]"
3. IMPACTO:    "Si metemos X, sacamos Y, y eso afecta [metrica]"
4. ALTERNATIVA: "Puedo priorizarlo para el proximo sprint / hacemos un MVP minimo"
5. DECISION:    "Si es realmente urgente, hablemos con el equipo sobre el trade-off"
```

> **Tip entrevista:** "Nunca digo 'no' a secas. Digo 'no ahora' con datos. Muestro el costo de oportunidad: meter algo al sprint significa sacar algo mas. Dejo que el stakeholder tome la decision informada."

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| Que es el Product Backlog? | Lista ordenada de todo el trabajo del producto, unica fuente del equipo |
| Formato de historia? | Como [usuario] quiero [accion] para [beneficio] |
| Que es INVEST? | Independent, Negotiable, Valuable, Estimable, Small, Testable |
| Priorizas con que tecnica? | RICE para datos cuantitativos, MoSCoW para alineamiento rapido |
| Que es refinamiento? | Preparar items del backlog: detallar, estimar, priorizar |
| Story Points miden? | Complejidad relativa, NO tiempo |
| Deuda tecnica? | Hacerla visible, regla del 20%, traducirla a impacto de negocio |
| Como splits epicas? | Por flujo, por regla de negocio, por rol, happy path + excepciones |
| Dices NO como? | Escuchar, dar contexto, mostrar impacto, ofrecer alternativa |
| DoR responsabilidad de? | El PO asegura que los items cumplan el DoR antes del sprint |

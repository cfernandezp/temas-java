# DISCOVERY, UX Y FIGMA

---

## 1. Que es Product Discovery?

**Product Discovery** es el proceso de entender que construir ANTES de construirlo. Busca responder 4 riesgos:

| Riesgo | Pregunta | Como se valida |
|--------|----------|---------------|
| **Valor** | Los usuarios lo quieren? | Entrevistas, encuestas, data analysis |
| **Usabilidad** | Los usuarios pueden usarlo? | Prototipos, tests de usabilidad |
| **Factibilidad** | Podemos construirlo? | Spike tecnico, consulta con devs |
| **Viabilidad** | El negocio puede sostenerlo? | Business case, ROI, regulacion |

**Actividades de Discovery:**

```
1. Research       → Entrevistas, encuestas, analisis de data
2. Ideacion       → Brainstorming, Design Sprint, Impact Mapping
3. Prototipado    → Wireframes, mockups, prototipos en Figma
4. Validacion     → Tests de usabilidad, A/B tests, fake door test
5. Definicion     → Historias de usuario, criterios de aceptacion
```

---

## 2. Tecnicas de User Research

### Investigacion cualitativa (entender el POR QUE)

| Tecnica | Que es | Cuando usarla |
|---------|--------|---------------|
| **Entrevista de usuario** | Conversacion 1:1 con usuario real | Entender necesidades, frustraciones |
| **Test de usabilidad** | Usuario intenta completar una tarea | Validar si el diseno es intuitivo |
| **Card sorting** | Usuario organiza items en categorias | Definir la navegacion/estructura |
| **Shadowing** | Observar al usuario en su contexto real | Entender el comportamiento natural |

**Template de entrevista de usuario (Apuesta Total):**

```
Objetivo: Entender como los usuarios eligen donde apostar

1. Cuentame sobre la ultima vez que hiciste una apuesta deportiva
2. Que plataforma usaste y por que la elegiste?
3. Que es lo mas frustrante del proceso de apostar?
4. Como depositas dinero normalmente? Que metodo prefieres?
5. Que haces cuando quieres retirar tus ganancias?
6. Que te haria cambiar de plataforma de apuestas?
7. Usas el celular o la computadora para apostar?

REGLAS:
- NO hacer preguntas con respuesta si/no
- NO sugerir respuestas
- ESCUCHAR mas que hablar (regla 80/20)
- Preguntar "por que?" y "cuentame mas"
```

### Investigacion cuantitativa (entender el CUANTO)

| Tecnica | Que es | Cuando usarla |
|---------|--------|---------------|
| **Analytics** | Datos de comportamiento (Google Analytics, Mixpanel) | Identificar patrones de uso |
| **Encuestas** | Cuestionarios a escala | Validar hipotesis con numeros |
| **Heatmaps** | Donde hacen clic los usuarios | Optimizar layout y CTAs |
| **Funnel analysis** | Donde se caen los usuarios en un flujo | Identificar puntos de friccion |

---

## 3. Design Thinking (proceso de 5 fases)

```
1. EMPATIZAR    → Investigar y entender al usuario
      ↓
2. DEFINIR      → Sintetizar insights en un problema claro
      ↓
3. IDEAR        → Generar multiples soluciones posibles
      ↓
4. PROTOTIPAR   → Construir una version rapida y barata
      ↓
5. TESTEAR      → Validar con usuarios reales
      ↓
   (Iterar segun lo aprendido)
```

**Herramienta: Problem Statement**

```
[Usuario] necesita [necesidad] porque [insight/razon].

Ejemplo:
"El apostador casual necesita depositar en menos de 1 minuto porque
apuesta impulsivamente durante un partido en vivo y si el proceso
es largo, abandona y pierde el momento."
```

---

## 4. Fundamentos de UX que el PO debe conocer

### Principios de usabilidad (Jakob Nielsen)

| Principio | Significado | Ejemplo en Apuestas |
|-----------|-------------|---------------------|
| **Visibilidad del estado** | El usuario sabe que pasa | Barra de progreso al depositar |
| **Consistencia** | Mismos patrones en toda la app | Boton "Apostar" siempre verde y en la misma posicion |
| **Prevencion de errores** | Evitar que el usuario se equivoque | Confirmar antes de apostar un monto alto |
| **Reconocimiento vs memoria** | Mostrar opciones, no pedirlas de memoria | Mostrar equipos favoritos, no pedir que los busque |
| **Flexibilidad** | Permitir atajos a usuarios expertos | Apuesta rapida con un clic para recurrentes |
| **Diseno minimalista** | No sobrecargar con informacion | Cuota clara, boton grande, sin distracciones |
| **Ayudar con errores** | Mensajes claros cuando algo falla | "Tu deposito fallo porque Yape esta en mantenimiento. Prueba con PagoEfectivo" |

### UX Writing (microcopy)

| Malo | Bueno |
|------|-------|
| "Error 500" | "Algo salio mal. Intenta de nuevo en unos segundos" |
| "Transaccion procesada" | "Deposito exitoso! Tu saldo es S/150" |
| "Input invalido" | "Ingresa un monto entre S/10 y S/5,000" |
| "Boton: Submit" | "Boton: Depositar S/50" |

---

## 5. Figma para Product Owners

### Que necesitas saber de Figma (NO necesitas disenar)

| Skill | Nivel PO | Para que |
|-------|----------|---------|
| **Navegar un proyecto** | Basico | Ver los disenos del equipo de UX |
| **Comentar en prototipos** | Basico | Dar feedback directo sobre el diseno |
| **Ver el prototipo interactivo** | Basico | Entender el flujo del usuario |
| **Inspect mode** | Intermedio | Ver medidas, colores, tipografia para validar |
| **Entender componentes** | Intermedio | Saber que es reutilizable y que es nuevo |
| **Crear wireframes basicos** | Intermedio | Comunicar ideas rapidas al equipo |

### Conceptos clave de Figma

```
PROYECTO → Archivo → Pagina → Frame → Componentes

Wireframe:    Esqueleto basico (blanco y negro, sin detalle)
Mockup:       Diseno visual final (colores, tipografia, imagenes)
Prototipo:    Mockup interactivo (se puede navegar, simula la app)
Componente:   Pieza reutilizable (boton, card, input)
Design System: Libreria de componentes estandar de la empresa
```

### Workflow PO + Disenador

```
1. PO define el problema y los criterios de aceptacion
2. Disenador crea wireframes low-fi
3. PO revisa y da feedback (en Figma con comentarios)
4. Disenador crea mockup high-fi
5. PO valida que cumple los criterios de negocio
6. Se hace test de usabilidad (si aplica)
7. Disenador hace el handoff al equipo de desarrollo
8. PO valida la implementacion vs el diseno
```

> **Tip entrevista:** "En Figma, mi rol no es disenar sino validar que los disenos resuelven el problema del usuario y cumplen los criterios de negocio. Uso los comentarios de Figma para dar feedback directo y evitar reuniones innecesarias."

---

## 6. Wireframes vs Mockups vs Prototipos

| Tipo | Fidelidad | Tiempo | Para que |
|------|-----------|--------|----------|
| **Wireframe** | Baja (cajas y texto) | 1-2 horas | Validar estructura y flujo |
| **Mockup** | Alta (visual final) | 1-2 dias | Validar look & feel |
| **Prototipo** | Interactivo | 2-3 dias | Validar experiencia completa |

**Cuando usar cada uno:**

```
Idea nueva, mucha incertidumbre  → Wireframe primero
Feature con diseno claro         → Mockup directo
Feature critica (pagos, registro)→ Prototipo + test de usabilidad
Bug fix / cambio menor           → No necesitas Figma
```

---

## 7. Jobs To Be Done (JTBD)

Framework que se enfoca en **para que "contrata" el usuario tu producto**.

**Formato:**

```
Cuando [situacion]
Quiero [motivacion]
Para [resultado esperado]
```

**Ejemplo Apuesta Total:**

```
Cuando estoy viendo un partido de futbol con mis amigos
Quiero apostar rapidamente al marcador
Para hacer el partido mas emocionante y tener la posibilidad de ganar dinero

Cuando gano una apuesta
Quiero retirar mi dinero inmediatamente
Para sentir que la ganancia es real y confiar en la plataforma
```

> **Tip entrevista:** "JTBD me ayuda a entender que el usuario no quiere 'un boton de deposito', quiere 'empezar a apostar ya'. Eso cambia completamente como priorizamos y disenamos."

---

## 8. Mapeo de experiencia del usuario

### User Journey Map

```
JOURNEY: PRIMER DEPOSITO EN APUESTA TOTAL

FASE:      Descubre  →  Registra  →  Verifica  →  Deposita  →  Apuesta
           ─────────────────────────────────────────────────────────────
ACCION:    Ve un     →  Llena     →  Sube DNI  →  Elige     →  Busca
           anuncio      formulario   y selfie     metodo       partido
                                                  de pago      y apuesta

EMOCION:   😊 Curioso → 😐 Neutro → 😤 Tedioso → 😊 Facil  → 😄 Emocion
                                     (si tarda)   (si Yape)

PAIN:      -          Piden      Rechazo de    No hay     Cuotas
                      muchos     foto, espera  Yape,      confusas
                      datos      verificacion  solo       para
                                               tarjeta    novatos

OPORTUNIDAD: CTA     Reducir    Verificacion  Agregar    Tutorial
             claro   campos     instantanea   Yape/Plin  de primera
                     al minimo  con IA        como       apuesta
                                              opciones
```

---

## 9. Fake Door Test y otras tecnicas de validacion rapida

| Tecnica | Como funciona | Ejemplo |
|---------|--------------|---------|
| **Fake Door** | Poner un boton/link a una feature que no existe y medir clics | Boton "Apostar con cripto" → medir interes antes de construirlo |
| **Wizard of Oz** | El usuario cree que es automatico pero lo haces manual | Chatbot de soporte que es un humano atras |
| **Concierge** | Servicio manual antes de automatizar | Retiros procesados manualmente antes de construir el sistema |
| **Landing Page** | Pagina de captura para medir demanda | "Proximamente: Casino Live" con formulario de interes |
| **Smoke Test** | Campana de marketing antes de tener el producto | Ads de "Apuesta Total App" midiendo descargas antes de tenerla |

---

## 10. Design Sprint (Google Ventures)

Proceso de 5 dias para resolver problemas de producto:

| Dia | Actividad | Output |
|-----|-----------|--------|
| **Lunes** | Entender el problema, mapear el journey | Mapa y target |
| **Martes** | Generar soluciones (sketches individuales) | Ideas diversas |
| **Miercoles** | Decidir la mejor solucion | Storyboard |
| **Jueves** | Construir un prototipo (Figma) | Prototipo realista |
| **Viernes** | Testear con 5 usuarios reales | Insights y decision |

> **Tip entrevista:** "No siempre se necesita un Design Sprint completo. Pero tomo prestados elementos: sketches rapidos, decision con dot voting, prototipos antes de desarrollar."

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| Que es Discovery? | Proceso de validar QUE construir antes de construirlo |
| 4 riesgos de Discovery? | Valor, usabilidad, factibilidad, viabilidad |
| Entrevistas de usuario? | Preguntas abiertas, escuchar 80%, no sugerir respuestas |
| Figma como PO? | Navegar, comentar, validar prototipos. NO disenar |
| Wireframe vs mockup? | Wireframe = estructura basica. Mockup = diseno visual final |
| JTBD? | Para que "contrata" el usuario tu producto |
| Fake Door Test? | Medir interes poniendo un boton a algo que no existe aun |
| User Journey Map? | Mapa visual del recorrido del usuario con emociones y pain points |
| Design Thinking? | Empatizar → Definir → Idear → Prototipar → Testear |
| UX Writing? | Microcopy claro: "Deposito exitoso!" en vez de "Transaccion procesada" |

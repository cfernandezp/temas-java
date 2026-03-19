# FUNDAMENTOS DE PRODUCT OWNERSHIP

---

## 1. Que es un Product Owner?

El **Product Owner (PO)** es el responsable de **maximizar el valor del producto** que entrega el equipo de desarrollo. Es la unica persona que gestiona el Product Backlog y representa la voz del cliente/negocio ante el equipo tecnico.

**Responsabilidades clave:**
- Definir y comunicar la **vision del producto**
- Gestionar y priorizar el **Product Backlog**
- Asegurar que el equipo entiende los items del backlog
- Tomar decisiones sobre que se construye y en que orden
- Aceptar o rechazar el trabajo terminado

> **Tip entrevista:** "El PO no es un tomador de pedidos. Es un estratega de producto que dice NO a muchas cosas para enfocar al equipo en lo que genera mas valor."

---

## 2. Diferencia entre Product Owner y Product Manager

| Aspecto | Product Owner | Product Manager |
|---------|--------------|-----------------|
| **Foco** | Ejecucion tactica (backlog, sprints) | Estrategia de producto (mercado, vision) |
| **Alcance** | Un equipo Scrum | Uno o multiples productos |
| **Interaccion principal** | Equipo de desarrollo | Stakeholders, clientes, mercado |
| **Marco** | Scrum (rol definido) | No depende de un framework |
| **Decide** | Que se construye en el sprint | Que se construye en los proximos meses/anos |

> **Tip entrevista:** "En la practica, especialmente en empresas medianas, el PO suele asumir ambos roles. En Apuesta Total, entiendo que el PO debe tener tanto vision estrategica como capacidad de bajar al detalle del backlog."

---

## 3. Que es la Vision del Producto?

La vision del producto es una **declaracion aspiracional** que describe:
- **Para quien** es el producto
- **Que problema** resuelve
- **Que lo diferencia** de las alternativas

**Formato clasico (Geoffrey Moore):**

```
Para [usuario objetivo]
Que necesita [necesidad/problema]
El [nombre del producto] es un [categoria]
Que [beneficio clave]
A diferencia de [competidor/alternativa]
Nuestro producto [diferenciador unico]
```

**Ejemplo aplicado a Apuesta Total:**

```
Para apostadores deportivos en Peru
Que buscan una experiencia segura, rapida y confiable
Apuesta Total es una plataforma de apuestas deportivas y casino online
Que ofrece las mejores cuotas, pagos inmediatos y una experiencia movil superior
A diferencia de casas de apuestas internacionales
Nuestra plataforma conoce al usuario peruano y ofrece metodos de pago locales
```

---

## 4. Que significa "maximizar el valor del producto"?

No se trata solo de construir features. **Maximizar valor** implica:

| Dimension | Ejemplo |
|-----------|---------|
| **Valor para el usuario** | Mejorar la experiencia de apuesta en vivo |
| **Valor para el negocio** | Aumentar la conversion de registro a primer deposito |
| **Reduccion de riesgo** | Implementar KYC para cumplir regulacion |
| **Reduccion de costo** | Automatizar procesos manuales de backoffice |
| **Aprendizaje** | Lanzar MVP para validar si los usuarios quieren un nuevo tipo de apuesta |

> **Tip entrevista:** "Maximizar valor no es hacer mas cosas, es hacer las cosas correctas. A veces el mayor valor esta en eliminar una feature que confunde al usuario."

---

## 5. Antipatrones del Product Owner (que NO hacer)

| Antipatron | Problema | Que hacer en su lugar |
|------------|----------|----------------------|
| **PO mesero** | Solo toma pedidos de stakeholders sin cuestionar | Validar con datos, priorizar por impacto |
| **PO ausente** | No esta disponible para el equipo | Asistir a las ceremonias, estar accesible |
| **PO proxy** | No tiene autoridad real para decidir | Escalar para tener autonomia de decision |
| **PO micromanager** | Dice al equipo COMO construir | Definir el QUE y el POR QUE, dejar el COMO al equipo |
| **PO sin datos** | Prioriza por intuicion o presion politica | Usar metricas, user research, experimentos |

---

## 6. Que habilidades debe tener un buen PO?

**Habilidades duras:**
- Escribir historias de usuario con criterios de aceptacion claros
- Manejar Jira, Figma, Excel, Power BI
- Entender arquitectura tecnica a alto nivel
- Analizar datos y metricas de producto
- Definir y medir KPIs/OKRs

**Habilidades blandas:**
- **Comunicacion**: traducir negocio a tecnico y viceversa
- **Negociacion**: decir no con fundamento
- **Empatia**: entender al usuario final
- **Liderazgo sin autoridad**: influir sin ser jefe del equipo
- **Toma de decisiones**: decidir rapido con informacion incompleta

> **Tip entrevista desde tu experiencia:** "Mi background tecnico en Java y banca me permite hablar el mismo idioma que los desarrolladores, lo que acelera el refinamiento y reduce malentendidos. A su vez, mi experiencia como TPO me permite traducir necesidades de negocio en soluciones tecnicas viables."

---

## 7. Como se mide el exito de un Product Owner?

No se mide por cuantas features entrega, sino por:

| Metrica | Que mide |
|---------|----------|
| **Valor entregado** | Impacto real en el usuario/negocio (ej: aumento de conversion) |
| **Satisfaccion del cliente** | NPS, CSAT, retencion |
| **Predictibilidad** | El equipo entrega lo comprometido sprint a sprint |
| **Salud del backlog** | Backlog priorizado, refinado y sin items zombies |
| **Alineamiento** | Stakeholders entienden el roadmap y las prioridades |
| **Velocidad de aprendizaje** | Tiempo entre hipotesis y validacion |

---

## 8. Diferencia entre Product Owner en Banca vs Apuestas

| Aspecto | Banca/Fintech | Apuestas/Gaming |
|---------|---------------|-----------------|
| **Regulacion** | SBS, BCRP, alta regulacion | MINCETUR, regulacion en crecimiento |
| **Usuarios** | Clientes bancarios (masivo, conservador) | Apostadores (engagement alto, impulsivo) |
| **Velocidad** | Releases planificados, cambios lentos | Iteracion rapida, eventos en tiempo real |
| **Metricas clave** | Transacciones, morosidad, NPS | GGR, depositos, apuestas activas, churn |
| **Riesgo** | Fraude financiero, compliance | Fraude, lavado de activos, juego problematico |
| **UX** | Funcional, seguro | Atractivo, gamificado, adictivo (con responsabilidad) |

> **Tip entrevista:** "Vengo de un entorno altamente regulado como banca, donde la precision y el compliance son criticos. Eso es un activo valioso en apuestas, donde la regulacion esta creciendo y el manejo de riesgos es igual de importante."

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| Que es un PO? | Responsable de maximizar el valor del producto gestionando el backlog |
| PO vs PM? | PO es tactico (sprint/backlog), PM es estrategico (mercado/vision) |
| Que es la vision? | Declaracion aspiracional de para quien, que problema resuelve y que lo diferencia |
| Maximizar valor? | No es hacer mas features, es hacer las correctas con datos |
| PO mesero? | Antipatron: solo toma pedidos sin cuestionar ni priorizar |
| Habilidad mas importante? | Comunicacion: traducir entre negocio y tecnologia |
| Como se mide el exito del PO? | Por valor entregado e impacto, no por cantidad de features |
| Banca vs Apuestas? | Apuestas es mas rapido, mas orientado a engagement, regulacion en crecimiento |

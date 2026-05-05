<!--
====================================================================
PRODUCTO N° 1 — PLAN DE TRABAJO
Consultoría: SCI N° 013-2026-CG-UE002/BID3
Consultor: Cristian Anthony Fernández Pérez
Fecha de presentación: 05 de mayo de 2026

Documento maestro en Markdown. Al finalizar se convierte a Word
con formato Anexo 1 §I del TDR (Arial 11, espacio simple, doble cara,
paginación inferior derecha, índice numerado, logo CGR en carátula).
====================================================================
-->

<!-- ============================ CARÁTULA ============================ -->

<!-- [LOGO CGR — esquina superior izquierda]      [LOGO BID — esquina superior derecha] -->

<div align="center">

**CONTRALORÍA GENERAL DE LA REPÚBLICA**

**PROYECTO "MEJORAMIENTO DE LOS SERVICIOS DE CONTROL GUBERNAMENTAL
PARA UN CONTROL EFECTIVO, PREVENTIVO Y FACILITADOR
DE LA GESTIÓN PÚBLICA" — BID 3**

Componente 1 · Acción de Inversión 1.8.2
*"Implementación del Sistema Integrado de los Servicios de Control Gubernamental — Fase 2"*

---

# PRODUCTO N° 1

# PLAN DE TRABAJO

---

**Consultoría:**
*"Contratación de un Consultor Ingeniero de Datos I para el Desarrollo del Módulo de Análisis de Datos para dar Soporte a los Auditores durante la Ejecución de los Servicios de Control en el Sistema Integrado de Control del Proyecto Interno 1.8.2"*

</div>

| Campo | Detalle |
|---|---|
| **Proceso de Selección** | SCI N° 013-2026-CG-UE002/BID3 |
| **Carta de Adjudicación** | N° 000133-2026-GPROY (20 de abril de 2026) |
| **Contrato** | N° 062-2026-CG-UE002/BID |
| **Suscripción del Contrato** | 28 de abril de 2026 |
| **Fecha de Presentación** | 05 de mayo de 2026 |
| **Consultor** | Cristian Anthony Fernández Pérez |
| **DNI** | 44911435 |
| **Profesión** | Ingeniero de Datos |
| **Dirigido a** | Subgerencia de Sistemas de Información y Analítica de Datos (SINFA) |
| **Con copia a** | Subgerencia de Gestión de Inversiones |

<div align="center">

Lima — Perú
Mayo 2026

</div>

<!-- Pie de página de carátula (se aplica en Word):
     Versión: 1.0  |  05/05/2026  |  Pág. 1 de N  -->

---

<!-- ====================== HISTORIAL DE REVISIONES ====================== -->

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor |
|---|---|---|---|
| 05/05/2026 | 1.0 | Elaboración inicial del Plan de Trabajo. | Cristian Anthony Fernández Pérez |

---

<!-- ============================ 2. ÍNDICE ============================ -->

# 2. Índice

<!-- Al pasar a Word, este índice se reemplazará por una Tabla de Contenido automática
     (Referencias > Tabla de contenido > Estilo Automático), que tomará los títulos
     y generará paginación automáticamente. -->

3. **Introducción**
4. **Objetivo de la consultoría**
   - 4.1 Objetivo general
   - 4.2 Objetivos específicos
5. **Metodología de trabajo**
   - 5.1 Marco normativo y técnico
   - 5.2 Enfoque del trabajo
   - 5.3 Arquitectura técnica (Anexo 03 del TDR)
   - 5.4 Stack tecnológico
   - 5.5 Buenas prácticas de ingeniería de datos
   - 5.6 Herramientas
   - 5.7 Confidencialidad y seguridad
   - 5.8 Coordinación y supervisión
6. **Productos a alcanzar**
   - 6.1 Cuadro resumen
   - 6.2 Producto N° 1 — Plan de Trabajo
   - 6.3 Producto N° 2 — Caso atípico 1: Ingesta inicial
   - 6.4 Producto N° 3 — Caso atípico 1: Fuentes complementarias y linaje
   - 6.5 Producto N° 4 — Caso atípico 2: Ingesta inicial
   - 6.6 Producto N° 5 — Caso atípico 2: Linaje, modelo y certificación
   - 6.7 Producto N° 6 — Caso atípico 3: Ingesta inicial
   - 6.8 Producto N° 7 — Caso atípico 3: Fuentes complementarias y linaje
   - 6.9 Producto N° 8 — Despliegue, matriz bus dimensional y validación
   - 6.10 Producto N° 9 — Validación, observaciones y transferencia de conocimiento
7. **Actividades a cumplir por cada producto**
   - 7.1 Producto N° 1 — Plan de Trabajo
   - 7.2 Producto N° 2 — Caso atípico 1: Ingesta inicial
   - 7.3 Producto N° 3 — Caso atípico 1: Fuentes complementarias y linaje
   - 7.4 Producto N° 4 — Caso atípico 2: Ingesta inicial
   - 7.5 Producto N° 5 — Caso atípico 2: Linaje, modelo y certificación
   - 7.6 Producto N° 6 — Caso atípico 3: Ingesta inicial
   - 7.7 Producto N° 7 — Caso atípico 3: Fuentes complementarias y linaje
   - 7.8 Producto N° 8 — Despliegue, matriz bus dimensional y validación
   - 7.9 Producto N° 9 — Validación, observaciones y transferencia de conocimiento
8. **Cronograma de productos y actividades**
   - 8.1 Cronograma maestro
   - 8.2 Diagrama de Gantt
   - 8.3 Plazos de revisión y subsanación
9. **Conclusiones y recomendaciones**
   - 9.1 Conclusiones
   - 9.2 Recomendaciones
10. **Anexos**
    - 10.1 Lista de abreviaturas y acrónimos
    - 10.2 Glosario de términos
    - 10.3 Diagrama de la Plataforma de Minería de Datos
    - 10.4 Diagrama de Gantt del cronograma

---

<!-- ============================ 3. INTRODUCCIÓN ============================ -->

# 3. Introducción

En el marco del **Contrato N° 062-2026-CG-UE002/BID**, suscrito el 28 de abril de 2026 con la Contraloría General de la República (CGR), presento el presente Plan de Trabajo correspondiente al **Producto N° 1** de la consultoría *"Contratación de un Consultor Ingeniero de Datos I para el Desarrollo del Módulo de Análisis de Datos para dar Soporte a los Auditores durante la Ejecución de los Servicios de Control en el Sistema Integrado de Control del Proyecto Interno 1.8.2"*.

La consultoría se enmarca en el Proyecto de Inversión Pública con Código Único de Inversiones (CUI) N° 2412703, denominado *"Mejoramiento de los servicios de control gubernamental para un control efectivo, preventivo y facilitador de la gestión pública"* (Proyecto BID 3), financiado mediante el Contrato de Préstamo N° 4724/OC-PE suscrito entre la República del Perú y el Banco Interamericano de Desarrollo (BID) el 5 de febrero de 2019. Específicamente, mi servicio se orienta al **Componente 1 — "Adecuados procesos para un control efectivo y eficiente"** y a la **Acción de Inversión 1.8.2 — "Implementación del Sistema Integrado de los Servicios de Control Gubernamental — Fase 2"**, cuya unidad orgánica responsable es la **Subgerencia de Sistemas de Información y Analítica de Datos (SINFA)**.

El presente documento tiene por finalidad establecer, de manera ordenada y verificable, el objetivo, la metodología, los productos, las actividades y el cronograma que asumiré durante los doscientos cuarenta (240) días calendario de ejecución del servicio. Su aprobación por parte de la SINFA constituye la línea base sobre la cual se medirá el avance, las conformidades parciales y el cierre final de la consultoría.

---

<!-- ====================== 4. OBJETIVO DE LA CONSULTORÍA ====================== -->

# 4. Objetivo de la consultoría

Conforme al numeral 3 de los Términos de Referencia, mi consultoría se orienta a los siguientes objetivos:

## 4.1 Objetivo general

Brindar soporte especializado como Ingeniero de Datos durante el desarrollo del componente *"Módulo de análisis de datos para dar soporte a los auditores durante la ejecución de los servicios de control"*, del proyecto interno 1.8.2, con la finalidad de disponer datos procesados y filtrados (Extracción, Transformación, Limpieza y Carga) provenientes de fuentes externas e internas, orientados a la identificación de casos atípicos en los que se transgrede una normativa vigente. La información resultante será de utilidad para la labor del auditor.

## 4.2 Objetivos específicos

- Identificar las fuentes externas e internas necesarias para el procesamiento de los datos.
- Realizar la limpieza y transformación de los datos en las capas **Plata** y **Oro** de la plataforma de minería de datos de la CGR.
- Elaborar el o los **DAG(s)** de los procesos ETL.
- Disponer los datos procesados y validados para ser consumidos por los tableros de control y/o el Sistema Integrado de Control Gubernamental (SICG).

---

<!-- ========================= 5. METODOLOGÍA DE TRABAJO ========================= -->

# 5. Metodología de trabajo

Mi metodología se ajusta a los lineamientos institucionales de la CGR establecidos por la Gerencia de Tecnologías de la Información y Gobierno Digital (GTIGD), bajo supervisión de la SINFA y en coordinación con la Subgerencia de Gestión de Inversiones, conforme al numeral 5 del TDR.

## 5.1 Marco normativo y técnico

- Procedimiento *"Desarrollo de Soluciones"* de la CGR.
- Estándares de Base de Datos vigentes de la CGR.
- Lineamientos de Seguridad de la Información con Proveedores de la CGR.
- Estándares técnicos de la GTIGD.
- Norma técnica peruana **NTP-ISO/IEC 12207** como referencia para el ciclo de vida del software, en línea con el Procedimiento *"Desarrollo de Soluciones"*.

## 5.2 Enfoque del trabajo

La consultoría se ejecuta de manera iterativa, considerando cada uno de los nueve productos como un mini-ciclo de entrega que recorre las siguientes fases, alineadas a la norma NTP-ISO/IEC 12207 y a la arquitectura medallón de la Plataforma de Minería de Datos:

| Fase | Propósito | Capa de datos asociada |
|---|---|---|
| **Identificación** | Catastro de fuentes externas e internas, esquemas, periodicidad y reglas de negocio. | — |
| **Ingesta** | Carga cruda al lakehouse preservando histórico y metadata. | Bronce |
| **Transformación** | Limpieza, casteo de tipos, normalización y controles de calidad. | Plata |
| **Modelado** | Construcción del modelo dimensional y agregaciones de negocio. | Oro |
| **Orquestación** | Encapsulamiento del ETL en DAG(s) de Apache Airflow. | Transversal |
| **Validación** | Verificación funcional con SINFA y aplicación del Anexo 02 del TDR. | — |
| **Entrega** | Publicación en GitLab CGR y presentación formal por Mesa de Partes Virtual. | Servicio |

Cada producto avanza de manera secuencial sobre estas fases, lo cual permite ir consolidando el lakehouse por caso atípico sin afectar la trazabilidad ni la idempotencia de los procesos previos.

## 5.3 Arquitectura técnica (Anexo 03 del TDR)

La Plataforma de Minería de Datos de la CGR sigue una arquitectura **medallón** con las siguientes capas:

| Capa | Función |
|---|---|
| Bronce | Ingesta cruda e histórica de fuentes |
| Plata | Filtrado, limpieza y refinado |
| Oro | Nivel de negocio y agregación |
| Servicio | Datos disponibles para consumo |
| Modelo Tabular | Modelos analíticos |
| Visualización | Tableros de control |

La ingesta admite modalidad **batch**, **CDC/CDF** y **streaming**, sobre fuentes estructuradas, semiestructuradas y no estructuradas.

## 5.4 Stack tecnológico

- **Almacenamiento:** Apache Hadoop (HDFS) + Delta Lake (Parquet).
- **Procesamiento:** PySpark y Spark SQL.
- **Orquestación:** Apache Airflow (DAGs).
- **Metastore:** HMS sobre MySQL.
- **Recursos del clúster:** Hadoop YARN.
- **Capa de servicio:** SQL Server + SSAS (modelo tabular).
- **Visualización:** Power BI.
- **Repositorio de código:** GitLab CGR.

## 5.5 Buenas prácticas de ingeniería de datos

- **Idempotencia:** procesos diseñados para re-ejecutarse sin duplicar datos.
- **Trazabilidad:** linaje completo de datos por capa, documentado en cada producto.
- **Calidad de dato:** controles explícitos en cada transición Bronce → Plata → Oro.
- **Versionamiento:** todo código fuente en el repositorio GitLab de la CGR conforme al estándar institucional (criterio de aceptación del Anexo 02 del TDR).
- **Documentación:** diccionario de datos, diagrama de linaje y matriz bus dimensional como entregables formales.

## 5.6 Herramientas

| Herramienta | Uso |
|---|---|
| GitLab CGR | Repositorio de código fuente y control de versiones (criterio Anexo 02). |
| Apache Airflow | Orquestación de los DAGs ETL. |
| IDE de desarrollo (Visual Studio Code u otro) | Edición de código PySpark/Spark SQL. |
| Mesa de Partes Virtual de la CGR | Canal oficial de presentación de productos a la SINFA. |
| Microsoft Teams / correo institucional | Coordinación con la SINFA y la Subgerencia de Gestión de Inversiones. |
| Microsoft Office 365 | Elaboración de la documentación técnica de los productos. |
| Power BI Desktop | Validación local de tableros previo al despliegue. |

## 5.7 Confidencialidad y seguridad

- Acceso únicamente en **modo consulta** a las bases de datos de la CGR (TDR §13).
- Cumplimiento estricto de las Políticas de Seguridad de la Información de la CGR y de los lineamientos para proveedores (TDR §14).
- Cesión exclusiva y gratuita a la CGR de todo producto generado durante la consultoría.

## 5.8 Coordinación y supervisión

- La **SINFA**, como Unidad Orgánica Responsable (UOR), establece los canales de coordinación, valida los entregables y otorga la conformidad de los productos.
- La **Subgerencia de Gestión de Inversiones** recibe copia de las entregas presentadas a través de la Mesa de Partes Virtual de la CGR.
- Los plazos de revisión y subsanación se rigen por el numeral 8 del TDR: hasta **5 días calendario** para la conformidad u observación por parte de la UOR; hasta **3 días calendario** para el levantamiento de observaciones por mi parte; hasta **2 oportunidades** de subsanación.

---

<!-- ========================= 6. PRODUCTOS A ALCANZAR ========================= -->

# 6. Productos a alcanzar

La consultoría comprende la elaboración y entrega de **nueve (9) productos** secuenciales que cubren los tres casos atípicos prioritarios identificados por la SINFA. La siguiente tabla resume el alcance global; en las fichas posteriores detallo el contenido literal de cada producto conforme al numeral 7 del TDR.

> **Nota:** El contenido específico de cada producto descrito en esta sección corresponde al alcance establecido en el numeral 7 del TDR. Dicho alcance podrá ser precisado o ajustado durante la ejecución del servicio, conforme a las directivas que emita la SINFA en su calidad de Unidad Orgánica Responsable, la GTIGD o las instancias superiores de la CGR, sin que ello implique modificación del número total de productos, los plazos ni el monto contractual.

## 6.1 Cuadro resumen

| N° | Producto | Caso atípico | Fuentes principales | Plazo (días) | Fecha límite |
|---|---|---|---|---|---|
| 1 | Plan de Trabajo | — | — | 7 | 05-05-2026 |
| 2 | Caso 1 — Ingesta inicial | Ausencia del ingeniero residente y/o supervisor | SSI, INFOBRAS | 30 | 28-05-2026 |
| 3 | Caso 1 — Fuentes complementarias y linaje | Ausencia del ingeniero residente y/o supervisor | MEF Inversiones, SIAF, MEF_ANDAT | 60 | 27-06-2026 |
| 4 | Caso 2 — Ingesta inicial | Sancionados SERVIR durante periodo de sanción | SERVIR, SUNEDU, RNP, AIRHSP | 90 | 27-07-2026 |
| 5 | Caso 2 — Linaje, modelo y certificación | Sancionados SERVIR durante periodo de sanción | SERVIR, SUNEDU, RNP, AIRHSP | 120 | 26-08-2026 |
| 6 | Caso 3 — Ingesta inicial | Beneficiarios de programas sociales que no corresponden | SISFOH, JUNTOS, PENSIÓN 65 | 150 | 25-09-2026 |
| 7 | Caso 3 — Fuentes complementarias y linaje | Beneficiarios de programas sociales que no corresponden | MEF, PLANILLAS, RENIEC | 180 | 25-10-2026 |
| 8 | Despliegue, matriz bus y validación de Casos 1 y 2 | Casos 1 y 2 | — | 210 | 24-11-2026 |
| 9 | Validación Caso 3, levantamiento de observaciones y transferencia | Caso 3 | — | 240 | 24-12-2026 |

> Las fechas que coincidan con día no laborable se trasladan al día hábil siguiente, conforme al numeral 8 del TDR. La retribución por cada producto se rige por el numeral 10 del TDR.

## 6.2 Producto N° 1 — Plan de Trabajo

**Plazo:** 7 días calendario · **Fecha límite:** 05/05/2026

Conforme al numeral 7 del TDR, el presente Plan de Trabajo detalla los productos específicos derivados del servicio y las actividades requeridas para alcanzarlos. Incluye:

- Objetivo de la consultoría.
- Actividades.
- Plazos.
- Cronograma de actividades (Gantt).
- Conclusiones y recomendaciones.
- Descripción de la metodología de referencia a emplear.
- Programación, plazos y productos sujetos a los Términos de Referencia.

## 6.3 Producto N° 2 — Caso atípico 1: Ingesta inicial

**Plazo:** 30 días calendario · **Fecha límite:** 28/05/2026

Documento técnico que sustenta los entregables correspondientes al caso atípico *"Ausencia del ingeniero residente y/o supervisor de la ejecución de la obra"*:

- Listado de fuentes externas e internas necesarias para el procesamiento de los datos.
- Scripts para la identificación de casos atípicos en fuentes de datos externas e internas: SSI, INFOBRAS, entre otras fuentes.
- Documento de mapeo de campos de fuentes externas e internas (SSI, INFOBRAS, entre otras) referidas a procesamiento ETL.

## 6.4 Producto N° 3 — Caso atípico 1: Fuentes complementarias y linaje

**Plazo:** 60 días calendario · **Fecha límite:** 27/06/2026

Documento técnico que sustenta los entregables adicionales del caso atípico *"Ausencia del ingeniero residente y/o supervisor de la ejecución de la obra"*:

- Listado de fuentes externas e internas adicionales necesarias para el procesamiento de los datos.
- Scripts para la identificación de casos atípicos en fuentes externas como Consulta de Inversiones del MEF, SIAF, MEF_ANDAT, entre otras.
- Documento de mapeo de campos de las fuentes externas (MEF, SIAF, MEF_ANDAT, entre otras) referidas a procesamiento ETL.
- Aplicaciones Python elaboradas para el procesamiento de los datos (SSI, INFOBRAS, MEF, SIAF, MEF_ANDAT, entre otras), basadas en PySpark y Spark SQL, bajo los estándares de la GTIGD de la CGR.
- Diagrama de carga de los datos en las diferentes capas de la plataforma de minería de datos de la CGR (linaje de los datos).

## 6.5 Producto N° 4 — Caso atípico 2: Ingesta inicial

**Plazo:** 90 días calendario · **Fecha límite:** 27/07/2026

Documento técnico que sustenta los entregables del caso atípico *"Sancionados SERVIR que están laborando o laboraron durante el periodo de sanción"*:

- Scripts para la identificación de casos atípicos en fuentes externas como SERVIR, SUNEDU, RNP, PLANILLAS AIRHSP, entre otras.
- Documento de mapeo de campos de las fuentes externas (SERVIR, SUNEDU, RNP, PLANILLAS AIRHSP, entre otras) referidas a procesamiento ETL.

## 6.6 Producto N° 5 — Caso atípico 2: Linaje, modelo y certificación

**Plazo:** 120 días calendario · **Fecha límite:** 26/08/2026

Documento técnico que sustenta los entregables del caso atípico *"Sancionados SERVIR que están laborando o laboraron durante el periodo de sanción"*:

- Diagrama de carga de los datos (SERVIR, SUNEDU, RNP, PLANILLAS AIRHSP, entre otras) en las diferentes capas de la plataforma de minería de datos de la CGR (linaje de los datos).
- Aplicaciones Python para el procesamiento de limpieza, transformación y carga de datos (SERVIR, SUNEDU, RNP, PLANILLAS AIRHSP, entre otras), basadas en PySpark y Spark SQL.
- Diagrama lógico de datos del modelo dimensional.
- Documentación técnica para el pase a certificación de los casos atípicos implementados (scripts, ETLs).

## 6.7 Producto N° 6 — Caso atípico 3: Ingesta inicial

**Plazo:** 150 días calendario · **Fecha límite:** 25/09/2026

Documento técnico que sustenta los entregables del caso atípico *"Beneficiarios de Programas Sociales que no corresponden"*:

- Scripts para la identificación de casos atípicos en fuentes externas como SISFOH, JUNTOS, PENSIÓN 65, entre otras.
- Documento de mapeo de campos de las fuentes externas (SISFOH, JUNTOS, PENSIÓN 65, entre otras) referidas a procesamiento ETL.
- Aplicaciones Python actualizadas para el procesamiento de limpieza, transformación y carga de datos, basadas en PySpark y Spark SQL.

## 6.8 Producto N° 7 — Caso atípico 3: Fuentes complementarias y linaje

**Plazo:** 180 días calendario · **Fecha límite:** 25/10/2026

Documento técnico que sustenta los entregables del caso atípico *"Beneficiarios de Programas Sociales que no corresponden"*:

- Scripts para la identificación de casos atípicos en fuentes externas como MEF, PLANILLAS, RENIEC, entre otras.
- Documento de mapeo de campos de las fuentes externas (MEF, PLANILLAS, RENIEC, entre otras) referidas a procesamiento ETL.
- Aplicaciones Python actualizadas para el procesamiento de limpieza, transformación y carga de datos, basadas en PySpark y Spark SQL.
- Diagrama de carga de los datos en las diferentes capas de la plataforma de minería de datos de la CGR (linaje de los datos).

## 6.9 Producto N° 8 — Despliegue, matriz bus dimensional y validación

**Plazo:** 210 días calendario · **Fecha límite:** 24/11/2026

Documento técnico que sustenta:

- Documentación técnica actualizada necesaria para el despliegue de la solución en los ambientes de certificación y producción.
- Matriz bus del modelo dimensional implementado (según dimensiones cargadas).
- Aplicaciones Python actualizadas.
- Documento técnico que sustente la validación de datos procesados para los casos atípicos:
    - Sancionados SERVIR que están laborando o laboraron durante el periodo de sanción.
    - Ausencia del ingeniero residente y/o supervisor en la ejecución de la obra.

## 6.10 Producto N° 9 — Validación, observaciones y transferencia de conocimiento

**Plazo:** 240 días calendario · **Fecha límite:** 24/12/2026

Documento técnico que sustenta:

- Informe de validación de datos procesados para el caso atípico *"Beneficiarios de programas sociales que no corresponden"*.
- Levantamiento de observaciones producto del proceso de certificación.
- Informe de transferencia de conocimiento al personal técnico, en referencia a los productos implementados (incluye material para la capacitación técnica).
- Documentos técnicos actualizados, de corresponder.

---

<!-- ================ 7. ACTIVIDADES A CUMPLIR POR CADA PRODUCTO ================ -->

# 7. Actividades a cumplir por cada producto

Las actividades que ejecutaré por producto se desprenden del listado del numeral 6 del TDR (actividades a–q) y se aplican según el contenido específico que cada producto debe sustentar conforme al numeral 7 del TDR.

## 7.1 Producto N° 1 — Plan de Trabajo

**Fecha límite:** 05/05/2026

- Lectura y análisis del TDR, la Carta de Adjudicación y el contrato suscrito.
- Identificación del marco normativo y técnico aplicable (Procedimiento *"Desarrollo de Soluciones"*, Estándares de BD CGR, Lineamientos GTIGD, NTP-ISO/IEC 12207).
- Definición de la metodología, el enfoque del trabajo, el stack tecnológico y las buenas prácticas a aplicar durante la consultoría.
- Estructuración del cronograma de los nueve productos a partir de los plazos del numeral 8 del TDR.
- Redacción y presentación del Plan de Trabajo a la SINFA por Mesa de Partes Virtual.

## 7.2 Producto N° 2 — Caso atípico 1: Ingesta inicial

**Fecha límite:** 28/05/2026

- Identificación de las fuentes externas e internas necesarias para el caso atípico — SSI e INFOBRAS [TDR §6.a].
- Coordinación con la SINFA para la habilitación de los accesos en modo consulta a las fuentes internas.
- Análisis exploratorio de la estructura, periodicidad y reglas de negocio de cada fuente.
- Carga cruda de los datos al lakehouse en la capa Bronce [TDR §6.b].
- Limpieza y transformación preliminar en la capa Plata [TDR §6.c].
- Elaboración de los scripts de identificación del caso atípico sobre SSI e INFOBRAS [TDR §6.d].
- Elaboración del documento de mapeo de campos Fuente → Destino [TDR §6.e].
- Versionamiento del código en el repositorio GitLab de la CGR [TDR §6.q].
- Documentación técnica del producto [TDR §6.j].

## 7.3 Producto N° 3 — Caso atípico 1: Fuentes complementarias y linaje

**Fecha límite:** 27/06/2026

- Identificación de las fuentes externas adicionales — Consulta de Inversiones del MEF, SIAF, MEF_ANDAT [TDR §6.a].
- Carga cruda al lakehouse de las fuentes adicionales [TDR §6.b].
- Limpieza y transformación en las capas Plata y Oro [TDR §6.c].
- Ampliación de los scripts de identificación del caso atípico considerando todas las fuentes [TDR §6.d].
- Mapeo de campos de las fuentes adicionales [TDR §6.e].
- Elaboración de las aplicaciones Python en PySpark y Spark SQL para el procesamiento integral del caso atípico (SSI, INFOBRAS, MEF, SIAF, MEF_ANDAT), bajo los estándares de la GTIGD.
- Elaboración del DAG de Apache Airflow correspondiente [TDR §6.g].
- Elaboración del diagrama de linaje de los datos en las capas de la plataforma [TDR §6.l].
- Versionamiento en GitLab CGR [TDR §6.q].
- Documentación técnica del producto [TDR §6.j].

## 7.4 Producto N° 4 — Caso atípico 2: Ingesta inicial

**Fecha límite:** 27/07/2026

- Identificación de las fuentes externas asociadas al caso atípico — SERVIR, SUNEDU, RNP, PLANILLAS AIRHSP [TDR §6.a].
- Coordinación con la SINFA para los convenios o accesos formales que requieran las fuentes externas.
- Análisis exploratorio de la estructura, periodicidad y reglas de las fuentes.
- Carga cruda al lakehouse en capa Bronce [TDR §6.b].
- Limpieza y transformación inicial en capa Plata [TDR §6.c].
- Elaboración de los scripts de identificación del caso atípico [TDR §6.d].
- Mapeo de campos de las fuentes [TDR §6.e].
- Versionamiento en GitLab CGR [TDR §6.q].
- Documentación técnica del producto [TDR §6.j].

## 7.5 Producto N° 5 — Caso atípico 2: Linaje, modelo y certificación

**Fecha límite:** 26/08/2026

- Consolidación de la carga de datos en las capas Bronce, Plata y Oro [TDR §6.b, §6.c].
- Elaboración de las aplicaciones Python en PySpark y Spark SQL para la limpieza, transformación y carga del caso atípico.
- Elaboración del DAG de Apache Airflow [TDR §6.g].
- Elaboración del diccionario de datos y del diagrama lógico del modelo dimensional [TDR §6.k].
- Elaboración del diagrama de linaje de los datos [TDR §6.l].
- Carga en la capa de servicio para consumo del SICG y tableros [TDR §6.f].
- Elaboración de la documentación técnica para el pase a certificación de los casos atípicos implementados [TDR §6.n].
- Versionamiento en GitLab CGR [TDR §6.q].

## 7.6 Producto N° 6 — Caso atípico 3: Ingesta inicial

**Fecha límite:** 25/09/2026

- Identificación de las fuentes externas — SISFOH, JUNTOS, PENSIÓN 65 [TDR §6.a].
- Coordinación con la SINFA para los accesos a las fuentes externas.
- Análisis exploratorio de las fuentes.
- Carga cruda al lakehouse [TDR §6.b].
- Limpieza y transformación inicial [TDR §6.c].
- Elaboración de los scripts de identificación del caso atípico [TDR §6.d].
- Mapeo de campos [TDR §6.e].
- Actualización de las aplicaciones Python para el procesamiento integral [TDR §6.i].
- Versionamiento en GitLab CGR [TDR §6.q].
- Documentación técnica del producto [TDR §6.j].

## 7.7 Producto N° 7 — Caso atípico 3: Fuentes complementarias y linaje

**Fecha límite:** 25/10/2026

- Identificación de las fuentes externas adicionales — MEF, PLANILLAS, RENIEC [TDR §6.a].
- Carga cruda al lakehouse [TDR §6.b].
- Limpieza y transformación en las capas Plata y Oro [TDR §6.c].
- Ampliación de los scripts de identificación del caso atípico [TDR §6.d].
- Mapeo de campos de las fuentes adicionales [TDR §6.e].
- Actualización de las aplicaciones Python en PySpark y Spark SQL [TDR §6.i].
- Elaboración o actualización del DAG de Apache Airflow [TDR §6.g].
- Elaboración del diagrama de linaje de los datos [TDR §6.l].
- Versionamiento en GitLab CGR [TDR §6.q].
- Documentación técnica del producto [TDR §6.j].

## 7.8 Producto N° 8 — Despliegue, matriz bus dimensional y validación

**Fecha límite:** 24/11/2026

- Actualización de las aplicaciones Python según retroalimentación de la SINFA y de los líderes usuarios [TDR §6.i].
- Carga en la capa de servicio (SQL Server) para los Casos 1 y 2 [TDR §6.f].
- Disposición de los datos para los tableros de control y el SICG [TDR §6.h].
- Elaboración de la matriz bus del modelo dimensional implementado [TDR §6.m].
- Elaboración o actualización de la documentación técnica para el despliegue en certificación y producción [TDR §6.n].
- Validación de los datos procesados para los Casos 1 y 2.
- Versionamiento en GitLab CGR [TDR §6.q].

## 7.9 Producto N° 9 — Validación, observaciones y transferencia de conocimiento

**Fecha límite:** 24/12/2026

- Validación de los datos procesados para el Caso 3.
- Levantamiento de observaciones derivadas del proceso de certificación [TDR §6.i].
- Atención de incidencias durante la puesta en producción de la solución implementada [TDR §6.o].
- Elaboración del informe de transferencia de conocimiento al personal técnico, incluyendo el material para la capacitación técnica [TDR §6.p].
- Actualización final de los documentos técnicos elaborados durante la consultoría.
- Cierre de versiones del repositorio GitLab CGR [TDR §6.q].

---

<!-- ============== 8. CRONOGRAMA DE PRODUCTOS Y ACTIVIDADES ============== -->

# 8. Cronograma de productos y actividades

El cronograma de la consultoría se construye a partir de los plazos del numeral 8 del TDR, contados a partir del día siguiente de la suscripción del contrato (28/04/2026 → día 1: 29/04/2026).

## 8.1 Cronograma maestro

| N° | Producto | Plazo (días) | Fecha inicio | Fecha límite |
|---|---|---|---|---|
| 1 | Plan de Trabajo | 7 | 29/04/2026 | 05/05/2026 |
| 2 | Caso 1 — Ingesta inicial | 30 | 06/05/2026 | 28/05/2026 |
| 3 | Caso 1 — Fuentes complementarias y linaje | 60 | 29/05/2026 | 27/06/2026 |
| 4 | Caso 2 — Ingesta inicial | 90 | 28/06/2026 | 27/07/2026 |
| 5 | Caso 2 — Linaje, modelo y certificación | 120 | 28/07/2026 | 26/08/2026 |
| 6 | Caso 3 — Ingesta inicial | 150 | 27/08/2026 | 25/09/2026 |
| 7 | Caso 3 — Fuentes complementarias y linaje | 180 | 26/09/2026 | 25/10/2026 |
| 8 | Despliegue, matriz bus dimensional y validación | 210 | 26/10/2026 | 24/11/2026 |
| 9 | Validación, observaciones y transferencia | 240 | 25/11/2026 | 24/12/2026 |

> Las fechas que coincidan con día no laborable se trasladan al día hábil siguiente, conforme al numeral 8 del TDR.

## 8.2 Diagrama de Gantt

El detalle gráfico del cronograma, con el desglose semanal por producto y la codificación por caso atípico, se presenta en el anexo correspondiente: **Anexo 10.5 — Diagrama de Gantt**.

## 8.3 Plazos de revisión y subsanación

Conforme al numeral 8 del TDR, los plazos institucionales aplicables al ciclo de revisión de cada producto son los siguientes:

| Etapa | Plazo |
|---|---|
| Conformidad de la UOR (SINFA) | Hasta 5 días calendario |
| Notificación de observaciones por la UOR | Hasta 5 días calendario |
| Levantamiento de observaciones por el consultor | Hasta 3 días calendario |
| Rondas máximas de subsanación | 2 oportunidades |

Estos plazos no se contabilizan dentro de los 240 días calendario del servicio, conforme al último párrafo del numeral 8 del TDR.

---

<!-- =================== 9. CONCLUSIONES Y RECOMENDACIONES =================== -->

# 9. Conclusiones y recomendaciones

## 9.1 Conclusiones

- El presente Plan de Trabajo establece el marco contractual, técnico y operativo para los 240 días calendario de la consultoría, y constituye la línea base sobre la cual la SINFA podrá medir el avance, las conformidades parciales y el cierre final del servicio.
- Los nueve productos definidos cubren de manera completa los tres casos atípicos prioritarios — ausencia del ingeniero residente y/o supervisor, sancionados SERVIR que laboran durante el periodo de sanción y beneficiarios de programas sociales que no corresponden — respetando los plazos del numeral 8 del TDR.
- La metodología propuesta se ajusta al Procedimiento *"Desarrollo de Soluciones"* de la CGR, a los Estándares de Base de Datos vigentes y a los lineamientos técnicos de la GTIGD, en línea con la norma NTP-ISO/IEC 12207.
- La arquitectura medallón (Bronce → Plata → Oro) sobre Delta Lake y el stack PySpark + Spark SQL + Apache Airflow + SQL Server + SSAS + Power BI son consistentes con la Plataforma de Minería de Datos descrita en el Anexo 03 del TDR.

## 9.2 Recomendaciones

A fin de garantizar la ejecución oportuna y completa de los productos contractuales, formulo las siguientes recomendaciones a la SINFA:

- **Habilitación temprana de accesos.** Disponer los accesos en modo consulta a las bases de datos de la CGR (TDR §13) durante los primeros días del servicio, a fin de iniciar sin demora la identificación de fuentes del Producto N° 2.
- **Estándares técnicos GTIGD.** Compartir formalmente los estándares aplicables (convenciones de nombramiento, rutas en HDFS, política de retries en Airflow, plantillas de DAG, configuración del repositorio GitLab) que permitan alinear la implementación a los lineamientos institucionales desde el primer entregable.
- **Coordinación con fuentes externas.** Coordinar oportunamente la disponibilidad de las fuentes externas (RENIEC, SUNEDU, MEF, SERVIR, AIRHSP, JUNTOS, PENSIÓN 65, SISFOH), considerando que su acceso puede requerir convenios o procesos administrativos previos.
- **Ventana de coordinación regular.** Definir una ventana semanal o quincenal de coordinación con la SINFA y la Subgerencia de Gestión de Inversiones para el seguimiento de avances, el levantamiento de bloqueos y la validación temprana de criterios de aceptación.
- **Líder usuario asignado.** Designar un líder usuario y/o analista de sistemas como contraparte funcional para validar reglas de negocio, mapeos de campos y resultados de los scripts de identificación de casos atípicos.

---

<!-- ============================ 10. ANEXOS ============================ -->

# 10. Anexos

## 10.1 Lista de abreviaturas y acrónimos

| Sigla | Descripción |
|---|---|
| AIRHSP | Aplicativo Informático para el Registro Centralizado de Planillas y de Datos de los Recursos Humanos del Sector Público |
| BID | Banco Interamericano de Desarrollo |
| BID 3 | Proyecto *"Mejoramiento de los servicios de control gubernamental para un control efectivo, preventivo y facilitador de la gestión pública"* |
| CDC / CDF | Change Data Capture / Change Data Feed |
| CGR | Contraloría General de la República del Perú |
| CUI | Código Único de Inversiones |
| DAG | Directed Acyclic Graph (flujo de trabajo de procesos ETL en Apache Airflow) |
| ETL | Extract, Transform, Load (procesos de extracción, transformación y carga de datos) |
| GTIGD | Gerencia de Tecnologías de la Información y Gobierno Digital |
| HDFS | Hadoop Distributed File System |
| HMS | Hive Metastore |
| INFOBRAS | Sistema de Información de Obras Públicas |
| JUNTOS | Programa Nacional de Apoyo Directo a los más Pobres |
| MEF | Ministerio de Economía y Finanzas |
| MEF_ANDAT | Plataforma de Análisis de Datos del MEF |
| NTP | Norma Técnica Peruana |
| PENSIÓN 65 | Programa Nacional de Asistencia Solidaria |
| RENIEC | Registro Nacional de Identificación y Estado Civil |
| RNP | Registro Nacional de Proveedores |
| SCI | Servicio de Consultoría Individual |
| SERVIR | Autoridad Nacional del Servicio Civil |
| SIAF | Sistema Integrado de Administración Financiera |
| SICG | Sistema Integrado de Control Gubernamental |
| SINFA | Subgerencia de Sistemas de Información y Analítica de Datos |
| SISFOH | Sistema de Focalización de Hogares |
| SNC | Sistema Nacional de Control |
| SSAS | SQL Server Analysis Services |
| SSI | Sistema de Seguimiento de Inversiones |
| SUNEDU | Superintendencia Nacional de Educación Superior Universitaria |
| TDR | Términos de Referencia |
| UE002 | Unidad Ejecutora 002 — Gestión de Proyectos y Fortalecimiento de Capacidades |
| UOR | Unidad Orgánica Responsable |
| YARN | Yet Another Resource Negotiator (gestor de recursos de Apache Hadoop) |

## 10.2 Glosario de términos

| Término | Definición |
|---|---|
| Arquitectura medallón | Patrón de organización del lakehouse en tres capas progresivas — Bronce, Plata y Oro — que separa el dato crudo, el dato refinado y el dato listo para el negocio. |
| Capa Bronce | Capa del lakehouse donde se almacenan los datos crudos e históricos tal como llegan desde la fuente, preservando la trazabilidad. |
| Capa Plata | Capa intermedia donde los datos son filtrados, limpiados, normalizados y refinados. |
| Capa Oro | Capa de negocio donde los datos se modelan dimensionalmente y se agregan para su consumo analítico. |
| Capa de Servicio | Capa de exposición de los datos procesados al consumo final por tableros y sistemas (en este proyecto, sobre SQL Server y SSAS). |
| Caso atípico | Hallazgo o conjunto de registros en los que se identifica una transgresión a una normativa vigente, conforme al numeral 3 del TDR. |
| Delta Lake | Capa de almacenamiento abierta que aporta transacciones ACID, control de versiones y manejo de esquemas sobre Apache Hadoop. |
| Idempotencia | Propiedad de un proceso ETL que permite re-ejecutarlo múltiples veces sobre los mismos datos sin generar duplicados ni alterar el resultado. |
| Lakehouse | Plataforma de datos que integra las capacidades analíticas de un data warehouse sobre el almacenamiento abierto de un data lake. |
| Linaje de datos | Trazabilidad documentada del flujo de los datos desde su origen hasta su consumo final, indicando transformaciones aplicadas en cada capa. |
| Matriz bus dimensional | Matriz que relaciona los procesos de negocio con las dimensiones conformadas que los soportan, base del diseño dimensional de Kimball. |
| Modelo dimensional | Modelo de datos analítico estructurado en tablas de hechos y dimensiones, optimizado para consultas de inteligencia de negocios. |
| PySpark | Interfaz de Python para Apache Spark, utilizada para el procesamiento distribuido de grandes volúmenes de datos. |
| Spark SQL | Módulo de Apache Spark para consultas estructuradas mediante el lenguaje SQL sobre datos distribuidos. |

## 10.3 Diagrama de la Plataforma de Minería de Datos

Referencia: **Anexo 03 del TDR** — Arquitectura de la Plataforma de Minería de Datos de la CGR. La descripción funcional de cada capa se desarrolla en la Sección 5.3 del presente Plan de Trabajo.

## 10.4 Diagrama de Gantt del cronograma

Archivo adjunto: **Gantt-P1.xlsx** — Cronograma detallado por semana con codificación por caso atípico y panel congelado para visualización extendida. Soporta el cuadro maestro presentado en la Sección 8.1.


# INDUSTRIA DE APUESTAS Y GAMING

---

## 1. Modelo de negocio de una casa de apuestas

### Como gana dinero una casa de apuestas?

La casa **NO apuesta contra el usuario**. Gana por el **margen matematico** (vigorish/vig) incluido en las cuotas.

```
Ejemplo simplificado:
- Partido: Alianza Lima vs Universitario
- Probabilidad real: 50% para cada uno
- Cuota justa: 2.00 para cada uno (1/0.50)

Pero la casa ofrece:
- Alianza Lima: 1.90
- Universitario: 1.90

Si 100 personas apuestan S/10 cada una (50 por equipo):
- Recauda: S/1,000
- Paga:    50 x S/10 x 1.90 = S/950
- Margen:  S/50 (5% de margen)
```

### Lineas de negocio

| Linea | Descripcion | % Revenue tipico |
|-------|-------------|-----------------|
| **Apuestas deportivas** | Pre-match y en vivo | 40-50% |
| **Casino online** | Slots, ruleta, blackjack | 30-40% |
| **Casino live** | Mesas con crupier real via streaming | 10-15% |
| **Otros** | Virtual sports, e-sports, loteria | 5-10% |

---

## 2. Conceptos clave de apuestas

| Termino | Definicion | Ejemplo |
|---------|-----------|---------|
| **Cuota (Odds)** | Multiplicador que indica cuanto ganas | Cuota 2.50 → apuestas S/10, ganas S/25 |
| **Cuota decimal** | Formato europeo (mas comun en LATAM) | 1.50, 2.00, 3.75 |
| **Cuota americana** | Formato USA (+/- sobre 100) | +150 (ganas 150 por cada 100), -200 (apuestas 200 para ganar 100) |
| **Cuota fraccional** | Formato britanico | 3/1 (ganas 3 por cada 1 apostado) |
| **Spread** | Handicap para equilibrar equipos | Peru -1.5 vs Bolivia (Peru debe ganar por 2+) |
| **Over/Under** | Apostar si habra mas o menos de X goles | Over 2.5 goles (3+ goles para ganar) |
| **Parlay / Combinada** | Multiples apuestas en una sola boleta | Alianza + Cristal + Universitario ganan (todas deben acertar) |
| **Cash Out** | Retirar la apuesta antes de que termine el evento | Si vas ganando, puedes cobrar un monto reducido |
| **Rollover** | Veces que debes apostar un bono antes de retirar | Bono S/50 con rollover x5 = apostar S/250 antes de retirar |
| **GGR** | Gross Gaming Revenue = apostado - pagado | Ingreso bruto |
| **NGR** | Net Gaming Revenue = GGR - bonos - costos | Ingreso neto |
| **Handle** | Total de dinero apostado | Volumen bruto |
| **Hold %** | GGR / Handle x 100 | Margen de la casa |
| **Vig / Juice** | Comision de la casa incluida en las cuotas | Tipicamente 5-10% |

---

## 3. Tipos de apuestas

### Apuestas deportivas

| Tipo | Descripcion | Ejemplo |
|------|-------------|---------|
| **1X2** | Gana local, empate, gana visitante | Alianza 1.80, Empate 3.40, U 4.20 |
| **Handicap** | Ventaja/desventaja de goles | Peru -1.5 cuota 2.10 |
| **Over/Under** | Mas o menos de X goles | Over 2.5 cuota 1.85 |
| **Ambos anotan** | Los 2 equipos marcan? | Si 1.70, No 2.10 |
| **Resultado exacto** | Marcador final | 2-1 cuota 8.00 |
| **Primera mitad** | Resultado solo del primer tiempo | 1X2 primer tiempo |
| **Goleador** | Quien marca gol | Guerrero gol 3.50 |
| **En vivo** | Apuestas durante el partido | Proximo gol, tarjetas, corners |

### Casino online

| Juego | Descripcion | RTP tipico |
|-------|-------------|-----------|
| **Slots** | Maquinas tragamonedas digitales | 95-97% |
| **Blackjack** | 21 contra el crupier | 99.5% |
| **Ruleta** | Apuesta a numero/color | 97.3% (europea) |
| **Baccarat** | Apuesta a jugador/banca | 98.9% |
| **Poker** | Variantes contra la casa o entre jugadores | Variable |

> **RTP (Return to Player):** Porcentaje que la maquina devuelve a largo plazo. RTP 95% = de cada S/100, devuelve S/95 (la casa gana S/5).

---

## 4. User Journey del apostador

```
AWARENESS       REGISTRO        VERIFICACION     DEPOSITO         APUESTA          RETIRO
─────────       ────────        ────────────     ────────         ───────          ──────
Ve un           Crea cuenta     Sube DNI,        Elige metodo:    Busca evento,    Solicita
anuncio o       con email,      selfie,          - Yape           selecciona       retiro,
recomendacion   telefono,       datos            - Tarjeta        cuota, define    elige metodo,
de un amigo     contrasena      personales       - PagoEfectivo   monto, confirma  espera
                                                 - Transferencia                   procesamiento

PAIN POINTS:
- Registro     → Muchos campos, verificacion lenta
- Deposito     → Pocos metodos, montos minimos altos
- Apuesta      → Interfaz confusa para novatos, cuotas no claras
- Retiro       → Demora en procesamiento, KYC adicional, limites
- En vivo      → Latencia, cuotas desactualizadas, app lenta
```

---

## 5. Regulacion en Peru

### MINCETUR y la Ley de Juegos de Casino y Maquinas Tragamonedas

| Aspecto | Detalle |
|---------|---------|
| **Ente regulador** | MINCETUR (Ministerio de Comercio Exterior y Turismo) |
| **Ley principal** | Ley 27153 y sus modificatorias |
| **Apuestas deportivas online** | En proceso de regulacion formal |
| **Requisitos** | Licencia, capital minimo, servidor en Peru, reportes |
| **Impuestos** | Impuesto a los juegos (varia segun modalidad) |
| **Edad minima** | 18 anos |

### Compliance que el PO debe considerar

| Requisito | Implementacion en producto |
|-----------|--------------------------|
| **KYC (Know Your Customer)** | Verificacion de identidad: DNI + selfie + datos personales |
| **AML/LAFT** | Anti lavado de activos: monitoreo de transacciones sospechosas |
| **Juego responsable** | Limites de deposito, autoexclusion, alertas de tiempo |
| **Menores de edad** | Verificacion de edad obligatoria |
| **Proteccion de datos** | Cumplimiento de Ley de Proteccion de Datos Personales (29733) |
| **Publicidad responsable** | No dirigida a menores, incluir mensajes de juego responsable |

> **Tip entrevista:** "Mi experiencia en banca con KYC, prevencion de fraude y LAFT se traslada directamente a apuestas. Ambas industrias tienen los mismos desafios regulatorios de verificacion de identidad y monitoreo de transacciones."

---

## 6. Juego Responsable (Responsible Gaming)

**Features obligatorias/recomendadas:**

| Feature | Descripcion |
|---------|-------------|
| **Limites de deposito** | Diario, semanal, mensual. El usuario los configura |
| **Limites de apuesta** | Monto maximo por apuesta |
| **Limites de perdida** | Alerta cuando pierde X monto |
| **Autoexclusion** | El usuario puede bloquearse por dias/meses/permanente |
| **Reality check** | Alerta cada X minutos: "Llevas 2 horas jugando" |
| **Historial de actividad** | Ver apuestas, depositos, perdidas acumuladas |
| **Enlace a ayuda** | Link visible a lineas de ayuda (ludopatia) |
| **Cooling-off period** | Periodo de enfriamiento despues de perdida grande |

> **Tip entrevista:** "El juego responsable no es solo un requisito legal, es una ventaja competitiva. Los usuarios confian mas en plataformas que los protegen. Y desde producto, estas features aumentan la retencion a largo plazo."

---

## 7. Stack tecnologico tipico en apuestas

| Componente | Tecnologia comun | Para que |
|-----------|------------------|---------|
| **Frontend** | React, Angular, Flutter (mobile) | Interfaz de usuario |
| **Backend** | Java/Spring Boot, Node.js, Go | Logica de negocio, APIs |
| **Base de datos** | PostgreSQL, Redis (cache), MongoDB | Datos transaccionales y cache |
| **Feeds de cuotas** | Proveedores: Betradar, BetConstruct | Cuotas en tiempo real |
| **Pagos** | Pasarelas: Culqi, MercadoPago, Yape API | Depositos y retiros |
| **Streaming** | WebSockets, Server-Sent Events | Cuotas en vivo, resultados |
| **Monitoreo** | Grafana, Datadog, ELK | Performance, alertas |
| **CDN** | CloudFlare, AWS CloudFront | Rendimiento global |
| **CI/CD** | Jenkins, GitHub Actions, Docker | Despliegues |

> **Tip entrevista:** No necesitas saber los detalles tecnicos, pero mencionar que entiendes la arquitectura demuestra que puedes hablar con el equipo de desarrollo. "Mi experiencia con microservicios en Java y Spring Boot me permite entender las limitaciones tecnicas y proponer soluciones viables."

---

## 8. Competencia en Peru

| Competidor | Tipo | Fortaleza |
|-----------|------|-----------|
| **Apuesta Total** | Local | Conocimiento del mercado peruano, marca reconocida |
| **Betsson** | Internacional | Experiencia global, gran catalogo |
| **1xBet** | Internacional | Agresividad en bonos y marketing |
| **Bet365** | Internacional | Mejor UX del mundo, apuestas en vivo |
| **Betano** | Internacional | Fuerte en LATAM, patrocinios deportivos |
| **Doradobet** | Local | Presencia fisica + online |
| **Te Apuesto** | Local (Intralot) | Red de puntos de venta |
| **Inkabet** | Local | Marca establecida, foco deportivo |

**Ventaja competitiva de Apuesta Total:**
- Marca local conocida
- Conocimiento del usuario peruano
- Metodos de pago locales
- Presencia fisica + digital
- Atencion en espanol
- Foco en Liga 1 y futbol sudamericano

---

## 9. Tendencias de la industria

| Tendencia | Descripcion | Impacto en producto |
|-----------|-------------|-------------------|
| **Mobile-first** | 70%+ del trafico viene de mobile | App nativa, responsive, UX tactil |
| **Apuestas en vivo** | 60%+ del revenue en algunos mercados | Baja latencia, UX fluida, cash out |
| **Personalizacion** | Cuotas y eventos sugeridos por IA | Algoritmos de recomendacion |
| **Gamificacion** | Achievements, leaderboards, misiones | Programa de fidelizacion |
| **Crypto/Wallets** | Pagos con criptomonedas | Nuevos metodos de deposito |
| **eSports** | Apuestas en League of Legends, CS2, Dota | Nuevo segmento joven |
| **Regulacion creciente** | Mas paises regulan apuestas online | Compliance como diferenciador |
| **Social betting** | Compartir apuestas, seguir tipsters | Features sociales |
| **Micro-betting** | Apostar en cada jugada (ej: proximo corner) | UX de baja latencia, muchas opciones |

---

## 10. Metricas de negocio especificas

### Unit Economics

```
LTV (Lifetime Value):
- Usuario promedio deposita S/200/mes
- Margen de la casa: 8%
- Revenue por usuario/mes: S/16
- Vida util promedio: 10 meses
- LTV = S/16 x 10 = S/160

CAC (Customer Acquisition Cost):
- Gasto en marketing: S/50,000/mes
- Nuevos usuarios que depositan: 1,000
- CAC = S/50

Ratio LTV/CAC = 160/50 = 3.2x ✓ (sostenible si > 3x)
```

### Segmentacion de usuarios

| Segmento | Comportamiento | Valor | Estrategia |
|----------|---------------|-------|-----------|
| **Ballenas (VIP)** | Alto deposito, alta frecuencia | Top 5%, 40% del GGR | Atencion personalizada, limites altos |
| **Regulares** | Deposito medio, frecuencia media | 30% usuarios, 35% GGR | Fidelizacion, bonos de recarga |
| **Casuales** | Deposito bajo, solo eventos grandes | 50% usuarios, 20% GGR | Facilitar deposito, enganchar con cuotas mejoradas |
| **Dormidos** | No depositan hace +30 dias | 15% usuarios | Reactivacion con bonos, notificaciones |

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| Como gana la casa? | Por el margen (vig) incluido en las cuotas, tipicamente 5-10% |
| Que es GGR? | Gross Gaming Revenue = total apostado - total pagado en premios |
| Que es RTP? | Return to Player: % que el juego devuelve a largo plazo |
| Que es rollover? | Veces que debes apostar un bono antes de poder retirarlo |
| Regulacion en Peru? | MINCETUR regula. KYC, AML y juego responsable son obligatorios |
| Juego responsable? | Limites de deposito, autoexclusion, reality checks, enlace a ayuda |
| Tendencia principal? | Mobile-first + apuestas en vivo (60%+ del revenue) |
| Ventaja de Apuesta Total? | Marca local, conoce al peruano, metodos de pago locales |
| Cash Out? | Retirar una apuesta antes de que termine el evento |
| Parlay? | Apuesta combinada: multiples selecciones, todas deben acertar |

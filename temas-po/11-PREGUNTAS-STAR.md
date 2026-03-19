# PREGUNTAS SITUACIONALES Y METODO STAR

---

## 1. Que es el metodo STAR?

El metodo **STAR** es una tecnica para responder preguntas de entrevista conductual de forma estructurada.

```
S - SITUACION:  Contexto (donde, cuando, que proyecto)
T - TAREA:      Tu responsabilidad especifica
A - ACCION:     Que hiciste concretamente
R - RESULTADO:  Que lograste (con numeros si es posible)
```

**Reglas:**
- Maximo 2 minutos por respuesta
- Usa ejemplos REALES de tu experiencia
- Siempre incluye numeros/resultados
- Habla en primera persona ("yo hice", no "nosotros hicimos")
- Prepara 8-10 historias STAR que cubran diferentes competencias

---

## 2. Pregunta: "Cuentame sobre un producto que llevaste de idea a produccion"

```
SITUACION:
Cuando estaba en Novopayment, empece a notar un patron que se repetia:
cada semana recibia mensajes de nuestros clientes fintech preguntando
"oye, como le aviso a mi usuario que su pago ya se proceso?". Algunos
armaban soluciones caseras con emails manuales, otros simplemente no
notificaban nada y sus usuarios terminaban llamando al call center a
preguntar si su plata habia llegado.

Era un dolor real. No era algo que alguien se invento en una sala de
reuniones — los clientes nos lo estaban pidiendo a gritos.

TAREA:
Mi jefe me dijo: "Fernando, investiga esto y ve si tiene sentido armar
algo". Basicamente me dieron carta blanca para validar la necesidad y,
si el caso era solido, liderar el desarrollo como TPO.

ACCION:
Lo primero que hice fue llamar a 5 de nuestros clientes fintech mas
activos. No les mande un formulario ni nada formal — los llame, les
dije "tengo 20 minutos, contame como le avisas hoy a tu usuario
cuando se procesa un pago". Las respuestas fueron reveladoras:
- Uno mandaba emails con un script manual que corria cada 2 horas
- Otro no notificaba nada, y su soporte atendia 200 llamadas/dia
  solo para confirmar estados de pago
- Otro queria push notifications pero no tenia equipo para armarlo

Con eso arme un backlog de 12 historias de usuario. No me complique,
fui al grano: la primera historia era literalmente "como usuario,
quiero recibir una notificacion push cuando mi pago se procesa, para
no tener que llamar a soporte". Los criterios de aceptacion los
escribi pensando en lo minimo que resolviera el dolor.

Priorizamos con MoSCoW junto con el equipo: el MVP era solo push y
email. Nada de SMS, nada de WhatsApp, nada de personalizar templates
— eso venia despues si funcionaba.

El equipo de arquitectura propuso usar Java con Spring Boot para el
servicio, integrandolo via API REST con la plataforma de pagos. Lo
planificamos en 3 sprints de 2 semanas.

Me sente con QA a armar pruebas de carga porque sabia que si esto no
aguantaba volumen, iba a ser peor que no tenerlo. Probamos con 10,000
notificaciones por minuto — ese era el pico que esperabamos en dias
de pago masivo.

Para el lanzamiento no quise arriesgar. Propuse hacer un canary release
al 10% de los clientes primero. Si algo fallaba, el impacto era
controlado. El equipo estuvo de acuerdo porque ya nos habia pasado
antes lanzar cosas "para todos" y tener que hacer rollback a las 3 AM.

RESULTADO:
- Lanzamos en 6 semanas, dentro del plazo que habiamos comprometido
- En el primer mes, 8 de nuestros clientes lo activaron sin que
  tuvieramos que insistir — ellos mismos pedian el acceso
- El cliente que tenia 200 llamadas diarias al soporte las redujo a
  120 en la primera semana, y a 80 en el segundo mes. Un 40% menos
- Comercial empezo a usar el servicio como argumento de venta:
  "nosotros te damos notificaciones incluidas". Cerraron 2 clientes
  nuevos ese trimestre que mencionaron eso como diferenciador
```

---

## 3. Pregunta: "Como priorizas cuando todos quieren ser prioridad 1?"

```
SITUACION:
Esto me paso un martes en Novopayment. Llego al daily y tenia 3
mensajes urgentes esperandome:
- Marketing me escribio a las 7 AM: "Necesitamos la campana de
  referidos ESTA semana, ya cerramos acuerdo con el influencer"
- Compliance me mando un correo con subject "URGENTE": habia un
  reporte regulatorio que tenia que estar listo para fin de mes, y
  faltaban 3 semanas pero el desarrollo tomaba 2
- Y el CEO me llamo directamente: queria un dashboard ejecutivo
  para la presentacion al directorio del proximo viernes

Los tres sentian que lo suyo era lo mas importante. Y tecnicamente,
los tres tenian razon desde su perspectiva.

TAREA:
Yo tenia que decidir que entraba al sprint que comenzaba ese dia.
El equipo tenia capacidad para 1.5 de esas 3 cosas, no para las 3.
Y lo mas importante: tenia que comunicar la decision sin que nadie
se sintiera ignorado o sin que se armara un conflicto politico.

ACCION:
Lo primero que hice fue no responder individualmente. Si les decia
a cada uno "si, lo vemos" iba a terminar comprometido con todo. En
vez de eso, convoque una reunion de 30 minutos esa misma tarde con
los 3. Les dije: "Los cite a los 3 porque tenemos 3 prioridades y
capacidad para 1.5. Quiero que tomemos la decision juntos."

En la reunion, arme una matriz simple en el pizarron con 3 columnas:
urgencia (tiene fecha limite dura?), valor de negocio (cuanta plata
genera o evita perder?), y riesgo de no hacerlo (que pasa si lo
postergamos?).

El reporte regulatorio fue facil: tenia deadline legal, si no lo
entregabamos habia riesgo de multa. No era negociable. Todos
estuvieron de acuerdo — eso era prioridad 1.

La campana de referidos era valiosa, pero cuando le pregunte a
Marketing "que pasa si la lanzamos en 2 semanas en vez de esta?",
reconocieron que el acuerdo con el influencer era flexible. Le
propuse programarla para el siguiente sprint con fecha confirmada
y lo acepto.

El dashboard del CEO era el mas delicado politicamente. No le
puedes decir al CEO "tu pedido es prioridad 3". Lo que hice fue
proponerle una alternativa inmediata: "Para la presentacion del
viernes te armo un reporte en Excel con los mismos datos. No es
bonito, pero tiene todo lo que necesitas. El dashboard automatizado
lo entregamos el mes que viene." Lo acepto sin problema.

Documente toda la decision y el razonamiento en Confluence, con la
matriz incluida, para que quedara registro de por que decidimos asi.

RESULTADO:
- El reporte regulatorio se entrego 5 dias antes del deadline.
  Compliance quedo tranquilo y evitamos una posible multa
- La campana de referidos se lanzo 2 semanas despues. Marketing
  consiguio 500 usuarios nuevos con la campana del influencer
- El CEO presento con el Excel y nadie noto la diferencia. El
  dashboard se entrego en Q2 y ahora se actualiza automaticamente
- Lo mas valioso: a partir de ahi, los 3 stakeholders empezaron a
  usar ese formato de "urgencia/valor/riesgo" antes de pedirme
  cosas. Ya llegaban con su propio analisis, lo que ahorro muchas
  conversaciones dificiles
```

---

## 4. Pregunta: "Como manejas cuando desarrollo dice que algo no es viable?"

```
SITUACION:
En MiBanco me toco un caso clasico. El area de negocio queria que
cuando un cliente hacia una transferencia, le llegara la notificacion
push al celular inmediatamente — en tiempo real. El argumento era
logico: "Yape lo hace, BCP lo hace, nosotros tambien tenemos que
hacerlo."

Me fui a hablar con el equipo de desarrollo y el tech lead me dijo
sin rodeos: "Fernando, el core bancario procesa en batch cada 5
minutos. No podemos hacer real-time a menos que cambiemos toda la
arquitectura, y eso es un proyecto de 6 meses."

O sea, tenia a negocio pidiendo algo "para ayer" y a desarrollo
diciendo que tomaba 6 meses. Tipico sandwich de PO.

TAREA:
Encontrar un punto medio que dejara al usuario contento, que fuera
tecnicamete viable sin redisenar todo, y que no tomara medio ano.

ACCION:
Primero me sente con el tech lead una hora a entender la limitacion
REAL. No la version resumida de "no se puede", sino el detalle
tecnico. Me explico que el core bancario procesaba las transacciones
en lotes cada 5 minutos, y que cambiar eso implicaba tocar el
middleware de integracion, el sistema de colas, y hacer pruebas de
regresion de todo el flujo transaccional. Era un proyecto de 3
sprints minimo, con riesgo alto.

Pero mientras conversabamos, le pregunte: "Y si en vez de cambiar
el core, reducimos el intervalo del batch de 5 minutos a 1 minuto?
Es viable?". Se quedo pensando y me dijo: "Eso si, es un cambio
de configuracion en el scheduler mas un ajuste en la cola de
mensajes. Lo hacemos en 3 dias."

Con esa alternativa me fui a negocio. No les hable de "batches" ni
de "schedulers" — les dije: "Miren, hoy la notificacion tarda hasta
5 minutos. Puedo tenerla en maximo 1 minuto la proxima semana. La
opcion de que sea instantanea como Yape existe, pero son 6 meses de
desarrollo. Que prefieren: 1 minuto en una semana o instantaneo en
6 meses?"

La respuesta fue obvia. El usuario final no nota la diferencia entre
"instantaneo" y "llega en 45 segundos".

RESULTADO:
- Lo entregamos en 1 semana en vez de 6 meses
- El tiempo de notificacion bajo de 5 minutos a menos de 1 minuto
  en promedio (la mayoria llegaba en 30-45 segundos)
- Negocio quedo satisfecho, el equipo no tuvo que redisenar nada
- Las quejas de clientes por "no me llega la notificacion" bajaron
  un 70% segun los reportes de soporte
- Aprendizaje que me quedo: siempre preguntarle al equipo tecnico
  "y si hacemos algo intermedio?" antes de aceptar un "todo o nada"
```

---

## 5. Pregunta: "Dame un ejemplo de una decision dificil que tomaste"

```
SITUACION:
Estabamos en Novopayment a mitad de un sprint, desarrollando una
integracion con un procesador de pagos nuevo — era un proveedor que
nos iba a permitir ofrecer pagos con QR a nuestros clientes fintech.
El equipo ya llevaba 3 dias trabajando en la integracion, habian
avanzado bastante con la documentacion de la API que el proveedor
nos habia compartido.

Y el jueves a las 4 de la tarde me llega un email del proveedor:
"Estimados, les informamos que estamos migrando a la version 3.0 de
nuestra API. La documentacion que tienen ya no aplica. La nueva
documentacion estara disponible el lunes." Asi, sin anestesia.

El equipo se quedo en shock. 3 dias de trabajo potencialmente tirados
a la basura.

TAREA:
Tenia que decidir rapido: seguimos adelante con lo que tenemos
(asumiendo que mucho del trabajo todavia servia), esperamos la nueva
documentacion y perdemos la semana, o buscamos otra alternativa?

El Sprint Goal era "habilitar un nuevo canal de pago". Si no lo
cumpliamos, era la segunda vez seguida que no cumpliamos un Sprint
Goal, y eso estaba afectando la credibilidad del equipo.

ACCION:
Pare lo que estaba haciendo y me reuni con el equipo esa misma tarde.
Les pregunte dos cosas:
1. "Del trabajo que llevan, cuanto se puede reutilizar con la API
   nueva?" — La respuesta fue: "Tal vez un 30%, pero no estamos
   seguros hasta que veamos la documentacion nueva."
2. "Si seguimos, cuanto mas necesitamos?" — "Dependiendo de los
   cambios, entre 3 y 8 dias. No podemos estimarlo sin la doc."

Ese rango de 3 a 8 dias me dijo todo: era demasiada incertidumbre.
Si apostabamos por el mejor caso (3 dias) y terminaba siendo 8, no
solo perdiamos este sprint sino que arrastraba al siguiente.

Revise el backlog y encontre algo: habia otro procesador de pagos
con el que ya teniamos una integracion parcial de un sprint anterior.
Estaba 60% lista. Le pregunte al equipo cuanto tomaba completarla:
"2 dias, maximo 3. Ya conocemos esa API." Era predecible y seguro.

Tome la decision: paramos la integracion del proveedor nuevo y
completamos la del procesador alternativo. Comunique a stakeholders
explicando que el Sprint Goal se iba a cumplir, pero con un
procesador diferente. Al proveedor nuevo lo movi al siguiente sprint,
cuando ya tendriamos la documentacion actualizada.

No fue una decision popular. El gerente comercial me dijo: "Pero el
proveedor nuevo tenia mejores tarifas." Le explique que preferia
entregar algo funcional ahora y lo mejor despues, que arriesgarme
a no entregar nada.

RESULTADO:
- Cumplimos el Sprint Goal: el canal de pago quedo habilitado con
  el procesador alternativo, funcionando en produccion
- En el sprint siguiente integramos al proveedor nuevo ya con la
  API v3.0 y fue mucho mas limpio — la nueva documentacion era
  mejor que la anterior
- Recuperamos la confianza del equipo: romper la racha de sprints
  fallidos fue importante para la moral
- Me quedo como regla personal: para integraciones con terceros,
  siempre tener un plan B identificado en el backlog. Los
  proveedores externos son la variable que menos controlas
```

---

## 6. Pregunta: "Como mides el exito de una feature despues del lanzamiento?"

```
SITUACION:
Cuando lanzamos el servicio de notificaciones push en Novopayment,
mi jefe me pregunto algo que parecia simple: "Fernando, esta
funcionando bien el servicio de push?" Y me di cuenta de que no
tenia una respuesta solida. Podia decir "si, no ha caido" pero eso
no era medir exito — eso era medir que no se rompio.

Habiamos invertido 6 semanas de un equipo de 4 personas en ese
servicio. Necesitaba demostrar con datos si valio la pena y decidir
si inversion adicional (agregar SMS y WhatsApp) tenia sentido.

TAREA:
Definir metricas claras de exito, montar la forma de medirlas, y
presentar resultados a stakeholders 4 semanas despues del lanzamiento.

ACCION:
Antes de que terminara el desarrollo (en el sprint de QA), me sente
un viernes a definir que significaba "exito" para este servicio.
Use el framework HEART de Google porque me obligaba a pensar en el
usuario, no solo en metricas tecnicas:

- Happiness: que el usuario este contento con las notificaciones.
  Target: CSAT mayor a 4 de 5 en una encuesta que mostrariamos
  despues de la primera semana de uso
- Engagement: que la gente realmente active las notificaciones.
  Target: 70% de usuarios las activan (no tenia benchmark, fue una
  estimacion basada en que competidores reportaban 60-75%)
- Adoption: que nuestros clientes fintech lo adopten. Target: 8
  clientes en 30 dias (teniamos 15 activos, asi que era pedir la
  mitad)
- Retention: que los usuarios que activan push vuelvan mas seguido.
  Target: 2x mas retorno que los que no activan
- Task Success: que las notificaciones realmente lleguen. Target:
  99% entregadas en menos de 30 segundos

Le pedi al equipo que instrumentaran metricas en el servicio para
poder trackear esto. Montamos dashboards en Grafana para Task
Success (eso lo veiamos en tiempo real) y lo demas lo sacabamos de
la base de datos con queries semanales.

A las 4 semanas, recopile todo, arme un reporte de 5 slides (una
metrica por slide, con target vs real vs conclusion) y lo presente
al equipo de liderazgo.

RESULTADO:
- Happiness: CSAT 4.2/5. Superamos el target. Los comentarios
  positivos eran basicamente "por fin me avisan cuando llega mi
  plata"
- Engagement: 65%. Quede un poco corto del 70%. Investigue y
  descubri que el flujo de activacion tenia 3 pasos y mucha gente
  abandonaba en el segundo. Propuse simplificarlo a 1 paso para
  el siguiente sprint
- Adoption: 8 clientes en 4 semanas. Justo en el target
- Retention: los usuarios con push retornaban 1.8x mas. Cerca del
  target pero no llegamos al 2x. Necesitabamos mas data
- Task Success: 99.3% entregadas en menos de 30 seg. Superado

Con esos numeros, el equipo de liderazgo aprobo presupuesto para
agregar SMS y WhatsApp como canales adicionales. La presentacion
con datos concretos hizo que la decision fuera facil — no fue un
"creemos que funciona", fue "los numeros dicen que funciona y
esto es lo que falta mejorar"
```

---

## 7. Pregunta: "Como manejas la deuda tecnica?"

```
SITUACION:
En MiBanco, teniamos un modulo del servicio de notificaciones que se
habia armado rapido como prueba de concepto 8 meses atras. La idea
original era: "armemos algo rapido para validar, y despues lo hacemos
bien." Pero como pasa siempre, "despues" nunca llego. El PoC paso a
produccion tal cual, con hardcodes, sin tests, sin retry logic, y con
un manejo de errores que basicamente era un try-catch generico que
logueaba "error" y seguia como si nada.

El resultado: 3 a 4 incidentes por mes. Algunos menores (notificaciones
que no llegaban a un grupo de usuarios), pero dos veces en los ultimos
3 meses se cayo en horario pico — viernes de quincena, cuando todo
el mundo esta revisando si le cayo el sueldo. Esas caidas afectaron
a 5,000 usuarios cada una.

El equipo estaba frustrado. Cada vez que tocabamos ese modulo, algo
se rompia. Y cuando habia incidente, dejabamos todo para apagar el
fuego.

TAREA:
Necesitaba convencer al gerente de producto (y por extension al
negocio) de dedicar un sprint completo a refactorizacion — o sea,
2 semanas sin entregar features nuevas. Y cualquiera que haya
intentado esto sabe que decirle al negocio "no vamos a entregar
nada visible por 2 semanas" es una conversacion dificil.

ACCION:
Sabia que con argumentos tecnicos no iba a convencer a nadie. "El
codigo esta mal" no es un argumento de negocio. Asi que hice lo
que cualquier PO deberia hacer: traduje el problema tecnico a plata
y tiempo.

Me sente un sabado a revisar los datos de los ultimos 3 meses:
- 12 incidentes relacionados con ese modulo
- Cada incidente tomaba en promedio 3.5 horas de 1 dev + 1 QA para
  diagnosticar y parchear. Eso son 40 horas de equipo en 3 meses
  solo en apagar incendios
- Las 2 caidas en horario pico generaron 180 tickets de soporte
  entre las dos. A un costo promedio de atencion de S/8 por ticket,
  eso era S/1,440 solo en soporte
- Ademas, esas 40 horas eran horas que NO se usaban en features
  nuevas. Traduje eso a: "ese modulo nos esta costando el equivalente
  a 1 feature por mes que dejamos de entregar"

Arme una presentacion de 3 slides:
1. El problema: cuantos incidentes, cuanto nos cuestan
2. La propuesta: 1 sprint de refactorizacion
3. El ROI: si reducimos incidentes de 4/mes a 1/mes, recuperamos
   30 horas/mes de capacidad, que equivale a 1.5 features mas por
   trimestre

El gerente miro los numeros y dijo: "Hagamoslo." No hizo falta
pelear.

Despues planificamos el sprint con el equipo. El tech lead propuso
separar el modulo en un microservicio independiente con su propia
base de datos, agregar tests unitarios e integration tests (que
no tenia NINGUNO), y poner monitoreo con alertas en Grafana para
detectar problemas antes de que el usuario los viera.

RESULTADO:
- Los incidentes bajaron de 4 por mes a 0-1 por mes. En los
  primeros 2 meses despues del refactor, tuvimos solo 1 incidente
  menor
- Recuperamos aproximadamente 30 horas/mes de capacidad del equipo.
  Eso se noto inmediatamente en la velocidad del equipo en los
  sprints siguientes
- El equipo estaba visiblemente mas contento. Ya no tenian miedo
  de tocar ese modulo. Uno de los devs me dijo: "Es la primera vez
  en meses que puedo trabajar en algo nuevo sin que me interrumpan
  con un incidente"
- El gerente quedo tan satisfecho que establecio una regla: 20% de
  la capacidad de cada sprint se reserva para deuda tecnica. Eso
  fue un cambio cultural importante — ya no habia que pelear cada
  vez para hacer refactorizacion
```

---

## 8. Pregunta: "Por que quieres pasar de banca/fintech a apuestas?"

```
RESPUESTA (conversacional, no STAR):

"Mira, yo llevo varios anos trabajando en banca y fintech, y la
verdad es que me ha dado una base muy solida. Manejo de flujos
transaccionales — depositos, retiros, pagos. Todo lo que tiene que
ver con compliance: KYC, prevencion de lavado de activos, regulacion
de la SBS. Productos digitales que manejan miles de transacciones
por dia. Y toda la parte de prevencion de fraude.

Pero soy honesto: la banca tiene ciclos largos y es muy conservadora.
Un cambio en produccion pasa por 14 aprobaciones y tarda 3 meses.
Yo quiero moverme mas rapido.

Lo que me atrae de apuestas es que tiene TODOS los mismos desafios
tecnicos que la banca — manejas plata del usuario, tienes que
verificar identidad, cumplir regulaciones, prevenir fraude — pero
le agregas un componente que me parece fascinante: todo pasa en
tiempo real. Las cuotas cambian cada segundo, un gol puede generar
un pico de trafico en 2 segundos, los usuarios esperan que la app
responda al instante. Es un nivel de dinamismo que en banca no
existe.

Y si hablo especificamente de Apuesta Total, me atrae porque es
una marca peruana que conoce al usuario local. No es lo mismo
competir como Bet365 que viene con un producto global, que competir
conociendo que el peruano quiere apostar por la Liga 1, quiere
pagar con Yape, y quiere soporte en su idioma y en su horario.

Ademas, veo que la industria de apuestas en Peru esta en un momento
clave: se esta regulando, esta creciendo, y necesita gente que
entienda compliance — y eso justamente es lo que yo traigo de banca.
No vengo a aprender compliance desde cero, vengo a aplicar lo que
ya se en un contexto nuevo.

Basicamente: quiero usar todo lo que aprendi en banca pero en una
industria que se mueve mas rapido y donde puedo tener mas impacto."
```

---

## 9. Pregunta: "Como te mantienes actualizado como Product Owner?"

```
RESPUESTA (conversacional):

"Te lo divido en 4 areas:

PRODUCTO: Leo Lenny's Newsletter todas las semanas, sigo a Marty
Cagan y su blog de SVPG — su libro 'Inspired' cambio bastante como
pienso el rol de producto. Tambien sigo a Teresa Torres y su enfoque
de Continuous Discovery. Lo que mas me gusta de ella es que te
obliga a hablar con usuarios todas las semanas, no solo cuando
arrancas un proyecto.

AGILE: Tengo certificacion Scrum y lo aplico todos los dias, pero
mas alla de la certificacion, lo que me mantiene actualizado es
la practica. Cada retrospectiva es una oportunidad de mejorar algo.
Participo en meetups de la comunidad agil local cuando puedo.

TECNOLOGIA: Aca tengo una ventaja porque vengo de desarrollo en
Java. No estoy escribiendo codigo todos los dias, pero entiendo
cuando el equipo me habla de microservicios, de APIs REST, de
problemas de latencia. Leo articulos sobre arquitectura y nuevas
herramientas, no para ser experto sino para poder tener
conversaciones informadas con el equipo tecnico. Si un dev me dice
'esto va a requerir un evento asincrono con Kafka', yo entiendo
que me esta diciendo y puedo evaluar alternativas.

INDUSTRIA: Para esta postulacion, por ejemplo, me meti a fondo en
el modelo de negocio de apuestas. Entiendo que es GGR, handle,
hold%, RTP. Baje la app de Apuesta Total y de 3 competidores,
me registre, hice apuestas reales con plata real para entender
el user journey completo. Creo que no puedes ser PO de un producto
que no usas."
```

---

## 10. Pregunta: "Que harias en tus primeros 90 dias como PO en Apuesta Total?"

```
RESPUESTA:

"Lo divido en 3 etapas. Me gusta la estructura de 30-60-90 porque
te obliga a tener paciencia al principio y generar resultados
rapidos al final.

PRIMEROS 30 DIAS — CIERRO LA BOCA Y ESCUCHO:

Esto es lo mas importante y donde mas POs la cagan: llegan el
primer dia queriendo cambiar todo sin entender nada.

Yo haria esto:
- Conocer a cada persona del equipo de desarrollo, QA, diseno.
  No solo como se llaman, sino como trabajan, que les frustra,
  que les gusta del producto actual
- Ser USUARIO del producto: registrarme, depositar, apostar,
  retirar. Hacer todo el journey varias veces. Anotar cada
  friccion que encuentre como usuario real
- Revisar el backlog completo: que hay ahi, como esta priorizado,
  hace cuanto que no se limpia
- Reunirme uno a uno con stakeholders: comercial, marketing,
  compliance, soporte. Preguntarles: "Cual es tu mayor dolor con
  el producto hoy? Que te gustaria que fuera diferente?"
- Revisar los tickets de soporte del ultimo mes: que se quejan
  los usuarios? Donde estan los patrones?
- Entender la arquitectura tecnica a alto nivel: que se puede
  cambiar rapido, que requiere un proyecto largo

DIAS 31-60 — DIAGNOSTICO Y QUICK WINS:

Ya escuche suficiente, ahora empiezo a proponer:
- Identificar 3-5 quick wins: cosas de alto impacto y bajo
  esfuerzo que puedo entregar rapido para generar confianza.
  Pueden ser bugs molestos que nadie arregla, mejoras de UX
  pequenas, o flujos que se pueden simplificar
- Si las ceremonias agiles no funcionan bien, proponer ajustes.
  Pero con cuidado — primero entiendo por que son como son antes
  de cambiarlas
- Montar un dashboard basico de metricas de producto si no existe:
  conversion de registro, tasa de primer deposito, retencion
  semanal, tickets de soporte por categoria
- Empezar a hacer discovery con usuarios reales: si puedo,
  entrevistar a 5-10 apostadores, ver como usan la app, que
  los frustra

DIAS 61-90 — DEMUESTRO VALOR:

- Entregar al menos 2-3 quick wins con impacto medible. Que el
  equipo y los stakeholders vean que las cosas se mueven
- Presentar el primer ciclo de OKRs del producto si no existen,
  o proponer ajustes si ya hay
- Armar un roadmap Now-Next-Later para el proximo trimestre:
  que estamos haciendo ahora, que viene despues, que es mas a
  futuro. Sin comprometer fechas exactas — comprometer outcomes
- Tener el backlog sano: priorizado, refinado, con historias
  claras y criterios de aceptacion que el equipo entienda
- Establecer una cadencia de reportes con stakeholders: que
  cada 2 semanas sepan en que estamos y por que

Mi objetivo al dia 90: que el equipo diga 'este PO entiende el
producto y nos hace la vida mas facil' y que los stakeholders
digan 'este PO me escucha y me muestra resultados'."
```

---

## 11. Pregunta: "Como manejas un stakeholder dificil?"

```
SITUACION:
En Novopayment tenia un gerente comercial — buena persona, pero
con una urgencia cronica. Cada semana tenia algo que era "para
ayer". Me escribia por WhatsApp a las 10 de la noche: "Fernando,
el cliente X necesita esta feature urgente, si no lo hacemos nos
cancelan." Y cuando revisabas, muchas veces no era realmente
urgente — era que el cliente le habia pedido algo y el no queria
decirle que tenia que esperar.

El problema era que cada vez que el equipo cambiaba de tarea a
mitad de sprint para atender estas "urgencias", perdiamos 2-3
dias de trabajo. No solo por el cambio de contexto, sino porque
habia que dejar algo a medias, y despues retomarlo era complicado.
El equipo estaba frustrado y la velocidad del sprint era
impredecible.

TAREA:
Necesitaba encontrar la forma de atender las urgencias reales (que
si existian) sin que cada pedido del gerente comercial desestabilizara
al equipo. Y tenia que hacerlo sin arruinar la relacion con el,
porque al final del dia, el traia a los clientes.

ACCION:
Lo primero que hice fue no confrontarlo. Si le decia "deja de
mandarme urgencias", me ganaba un enemigo. En vez de eso, lo
invite a la siguiente Sprint Review. Queria que viera con sus
propios ojos como funcionaba el proceso y que entendiera que
cuando el pedia algo urgente, otra cosa se caia del sprint.

En la Review, el equipo mostro lo que habian entregado y lo que
NO habian podido entregar — y explicitamente mencionaron que 2
historias se habian postergado por una urgencia comercial de la
semana 1. Vi que el gerente comercial se quedo callado. Creo
que fue la primera vez que vio el impacto real de sus pedidos.

Despues de la Review, lo invite a un cafe y le mostre los datos
con calma: "En los ultimos 3 sprints, tuvimos 15 urgencias
comerciales. De esas 15, solo 4 eran realmente criticas — las
otras 11 podian haber esperado 1-2 semanas sin que pasara nada.
Pero las 15 nos costaron en total 12 dias de trabajo del equipo."

No lo dije en tono de reclamo. Le dije: "Yo entiendo que tu
trabajo es responder rapido a los clientes, y quiero ayudarte a
hacer eso sin que el equipo se desestabilice."

Le propuse un acuerdo:
1. Reservamos 15% de la capacidad del sprint para urgencias
   comerciales. Eso eran como 12-15 story points de colchon
2. Creamos un canal de Slack llamado "urgencias-comercial" con
   un template simple: nombre del cliente, que necesita, para
   cuando, cuanta plata esta en juego
3. Si las urgencias de la semana superaban el 15%, el tenia que
   decidir que urgencia se bajaba. No yo — el. Porque si todas
   son urgentes, ninguna es urgente
4. Nos reuniamos 15 minutos cada lunes a revisar su pipeline
   de urgencias para la semana

Lo acepto. Creo que el hecho de que le di un canal formal y
capacidad reservada lo hizo sentir escuchado — ya no necesitaba
ir por WhatsApp a las 10 PM.

RESULTADO:
- Las "urgencias" bajaron de 5 por sprint a 1-2 por sprint. Cuando
  tenia que llenar el template y poner "cuanta plata esta en juego",
  muchas veces se daba cuenta solo de que podia esperar
- El equipo recupero predictibilidad. La velocidad del sprint se
  estabilizo y la moral mejoro notablemente
- El gerente comercial se sintio atendido. Tenia un canal claro,
  capacidad reservada, y una reunion semanal donde podia plantear
  sus necesidades
- La relacion mejoro mucho. Paso de ser una relacion de tension a
  una de colaboracion. Incluso empezo a avisarme con 2-3 semanas
  de anticipacion cuando veia que un cliente iba a pedir algo grande
```

---

## 12. Pregunta: "Cual es tu mayor debilidad como PO?"

```
RESPUESTA (honesta, no cliche):

"Mi formacion es muy tecnica. Yo vengo de programar en Java durante
anos, de arquitectura de microservicios, de hablar con devs sobre
APIs y bases de datos. Eso me da una ventaja enorme como PO porque
entiendo las limitaciones y puedo proponer alternativas tecnicas.

Pero el otro lado de la moneda es que durante mucho tiempo descuide
el lado de UX y diseno. Me pasaba que en las reuniones con
disenadores me quedaba callado porque no tenia el vocabulario ni
el criterio para opinar sobre flujos de usuario, jerarquia visual,
o interacciones. Confiaba 100% en lo que el disenador proponia, y
eso no esta mal, pero como PO deberia poder cuestionar y co-crear,
no solo aceptar.

Lo estoy trabajando activamente. Estoy aprendiendo Figma — no para
disenar yo, sino para poder abrir un archivo, entender las
decisiones, y colaborar con el disenador en su lenguaje. Tome un
curso de Design Thinking y otro de UX Writing. Y en mis ultimos
proyectos me he involucrado mas en el discovery: sentarme con
usuarios, ver como usan el producto, entender sus frustraciones.

Creo que la combinacion de alguien que entiende la tecnologia Y
entiende al usuario es muy poderosa. Ya tengo lo primero, estoy
construyendo lo segundo. Y mientras tanto, compenso colaborando
muy de cerca con el equipo de diseno y nunca tomando decisiones
de UX solo — siempre las valido con datos o con usuarios."

(Tip: siempre elige una debilidad real que estes trabajando
activamente. Nunca digas "soy muy perfeccionista" o "trabajo
demasiado" — eso no es creible y demuestra falta de autoconciencia)
```

---

## Banco de historias STAR adicionales para preparar

| Competencia | Pregunta tipica | Historia que puedes usar |
|-------------|----------------|--------------------------|
| **Liderazgo** | "Cuando lideraste sin autoridad?" | Novopayment: coordinar 3 equipos para la integracion de pagos, donde ninguno me reportaba pero logre alinearlos con un objetivo comun |
| **Conflicto** | "Un desacuerdo con un colega?" | MiBanco: el arquitecto queria reescribir todo el servicio de cero, yo queria refactorizar incremental. Presentamos ambas opciones con datos y ganamos con el enfoque incremental |
| **Fracaso** | "Un proyecto que fallo?" | Feature de reportes personalizados que lance sin hacer discovery — solo 3 de 200 clientes la usaron. Aprendi que "el cliente lo pidio" no significa que lo va a usar |
| **Presion** | "Trabajo bajo presion extrema?" | Incidente en MiBanco un viernes de quincena: el servicio de notificaciones se cayo a las 5 PM, 15,000 usuarios afectados. Coordine la respuesta, comunicacion a stakeholders, y resolucion en 2 horas |
| **Innovacion** | "Cuando propusiste algo nuevo?" | Automatice el reporte semanal de metricas que antes se armaba a mano en Excel (4 horas/semana) con un dashboard en Grafana que se actualizaba solo |
| **Datos** | "Decision basada en datos?" | Use RICE scoring para priorizar 15 features pendientes. El analisis revelo que la feature que Marketing mas queria (chat en vivo) tenia el menor score, y la que soporte pedia (FAQ mejorado) tenia el mayor. Lanzamos FAQ y los tickets bajaron 25% |
| **Cambio** | "Adaptacion rapida?" | El cambio de API del proveedor a mitad de sprint (historia #5 de este doc) |
| **Cliente** | "Resolviste un problema del cliente?" | El servicio de notificaciones push para fintechs (historia #2 de este doc) |

---

## Tips finales para la entrevista

1. **USA la app de Apuesta Total**: registrate, deposita, apuesta, retira. Identifica 2-3 mejoras concretas que propondrias. Si en la entrevista dices "yo me baje la app, me registre, y encontre que el flujo de retiro tiene 6 pasos cuando podria tener 3" — eso vale mas que cualquier teoria

2. **Prepara preguntas para ELLOS**: muestra que te interesa entender, no solo ser contratado
   - "Cual es el mayor desafio del producto hoy?"
   - "Como esta estructurado el equipo de producto?"
   - "Cuales son los OKRs de este trimestre?"
   - "Cual es la relacion entre el equipo de producto y el regulador?"

3. **Conecta SIEMPRE con tu experiencia**: cada respuesta deberia terminar con algo como "...y eso es algo que ya resolvi en banca/fintech porque..."

4. **Se honesto con los gaps**: es mejor decir "no conozco la industria a fondo, pero he investigado, me baje la app, y entiendo el modelo de negocio" que fingir que eres experto en algo que no eres

5. **Muestra hambre**: "me emociona aprender una industria nueva donde puedo aportar mi experiencia" transmite mas que un CV perfecto

6. **Numeros siempre**: "mejore el proceso" no dice nada. "Reduje los incidentes de 4 a 1 por mes, liberando 30 horas de equipo" dice todo

7. **No memorices**: estas historias son una guia. En la entrevista contala con tus palabras, como si le estuvieras contando a un amigo. Si suena recitado, pierde toda la credibilidad

---

## Respuestas Rapidas

| Pregunta | Respuesta clave |
|----------|----------------|
| Metodo STAR? | Situacion, Tarea, Accion, Resultado. Max 2 minutos, siempre con numeros |
| Mayor fortaleza? | Background tecnico + experiencia de producto = hablo los dos idiomas y puedo sentarme con devs y con negocio |
| Mayor debilidad? | UX/Diseno — lo estoy trabajando con Figma, Design Thinking y mas participacion en discovery |
| Por que apuestas? | Mismos desafios que banca (pagos, compliance, fraude) pero mas rapido, mas dinamico, mas engagement |
| Primeros 90 dias? | 30: cierro la boca y escucho. 60: diagnostico y quick wins. 90: ejecuto y demuestro valor |
| Stakeholder dificil? | Datos + proceso + alternativas. Nunca confrontar, siempre ofrecer una solucion |
| Feature que fallo? | La cuento con honestidad, digo que aprendi, y explico como aplico esa leccion hoy |
| Te mantienes actualizado? | Lenny's, SVPG, Teresa Torres, comunidades agiles, y background tecnico que me permite entender al equipo |

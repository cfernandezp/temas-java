# Sector Financiero y Microfinanzas

---

## 1. Conceptos basicos del sector

| Concepto | Descripcion |
|----------|-------------|
| **Core bancario** | Software central que gestiona todas las operaciones del banco |
| **Microfinanzas** | Servicios financieros para personas y microempresas de bajos recursos |
| **Caja Municipal** | Entidad financiera municipal que ofrece ahorro y credito a pymes y personas |
| **Cooperativa de ahorro** | Institucion financiera de propiedad de sus socios |
| **SBS** | Superintendencia de Banca, Seguros y AFP (regulador en Peru) |
| **BCRP** | Banco Central de Reserva del Peru |
| **Encaje legal** | Porcentaje de depositos que la entidad debe mantener como reserva |
| **Tasa de interes activa** | Tasa que cobra la entidad por prestamos |
| **Tasa de interes pasiva** | Tasa que paga la entidad por depositos |
| **Spread financiero** | Diferencia entre tasa activa y pasiva (margen del banco) |
| **Mora** | Retraso en el pago de una obligacion |
| **Provision** | Reserva contable para cubrir posibles perdidas por creditos morosos |
| **TCEA** | Tasa de Costo Efectivo Anual (incluye comisiones y seguros) |
| **TEA** | Tasa Efectiva Anual (solo intereses) |

---

## 2. Productos financieros tipicos

### Creditos
| Tipo | Descripcion |
|------|-------------|
| **Credito PYME** | Para microempresas (capital de trabajo, activos fijos) |
| **Credito personal** | Consumo personal |
| **Credito hipotecario** | Para vivienda |
| **Credito grupal/solidario** | Grupo de personas se garantizan mutuamente |
| **Credito agricola** | Para actividades agropecuarias |

### Ahorros
| Tipo | Descripcion |
|------|-------------|
| **Cuenta de ahorros** | Deposito a la vista con intereses |
| **Plazo fijo** | Deposito a plazo con tasa fija |
| **CTS** | Compensacion por Tiempo de Servicio |
| **Cuenta corriente** | Para empresas, con chequera |

### Operaciones
| Tipo | Descripcion |
|------|-------------|
| **Transferencia** | Mover fondos entre cuentas |
| **Pago de servicio** | Pago de agua, luz, telefono |
| **Giro** | Envio de dinero a otra plaza |
| **Cambio de moneda** | Compra/venta de dolares |

---

## 3. Arquitectura tipica de una caja municipal

```
                        ┌──────────────────────────────────────────┐
                        │            CANALES DIGITALES             │
                        │  App Movil  │  Web Banking  │  Chatbot  │
                        └──────────────┬───────────────────────────┘
                                       │
                        ┌──────────────▼───────────────────────────┐
                        │           API GATEWAY                     │
                        │  Rate Limit │ OAuth2 │ WAF │ Logging     │
                        └──────────────┬───────────────────────────┘
                                       │
        ┌──────────────────────────────▼───────────────────────────────┐
        │                    MIDDLEWARE (Camel/Fuse)                     │
        │                                                               │
        │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
        │  │ Orquestacion │  │Transformacion│  │  Validacion/Reglas  │  │
        │  └──────┬───────┘  └──────┬───────┘  └──────────┬──────────┘  │
        │         │                 │                      │            │
        │  ┌──────▼─────────────────▼──────────────────────▼──────────┐ │
        │  │              AMQ / Kafka (Mensajeria)                     │ │
        │  └───────────────────────────────────────────────────────────┘ │
        └──────────────────────────────┬───────────────────────────────┘
                                       │
        ┌──────────────────────────────▼───────────────────────────────┐
        │                    CORE BANCARIO (Bantotal)                    │
        │  Clientes │ Cuentas │ Creditos │ Operaciones │ Contabilidad  │
        └──────────────────────────────┬───────────────────────────────┘
                                       │
        ┌──────────────────────────────▼───────────────────────────────┐
        │                  SISTEMAS EXTERNOS                            │
        │  SBS │ RENIEC │ Sentinel │ Centrales de Riesgo │ SUNAT      │
        └──────────────────────────────────────────────────────────────┘
```

---

## 4. Integraciones tipicas del middleware

| Integracion | Protocolo | Descripcion |
|-------------|-----------|-------------|
| **Core Bantotal** | REST/SOAP | Operaciones bancarias principales |
| **SBS (reportes regulatorios)** | SFTP/Web Service | Envio de reportes (Anexos) |
| **RENIEC** | REST/SOAP | Validacion de identidad (DNI) |
| **Sentinel / Centrales de riesgo** | REST | Consulta de historial crediticio |
| **SUNAT** | REST | Validacion de RUC |
| **Pasarela de pagos** | REST | Pagos con tarjeta, QR |
| **Notificaciones** | REST/SMTP/SMS | Envio de alertas y OTP |
| **ATM/POS** | ISO 8583 | Transacciones en cajeros y puntos de venta |
| **Transferencias interbancarias** | CCE/LBTR | Camara de compensacion |
| **Seguros** | REST/SOAP | Polizas de desgravamen, SOAT |

---

## 5. Procesos batch tipicos (nocturnos)

```java
// Proceso de cierre de dia: se ejecuta todas las noches
from("timer:cierreDia?period=86400000") // Cada 24 horas
    .routeId("procesoCierreDia")
    .log("Iniciando cierre de dia: ${date:now:yyyy-MM-dd}")

    // 1. Calculo de intereses
    .to("direct:btCalcularInteresesDiarios")

    // 2. Actualizacion de mora
    .to("direct:btActualizarMora")

    // 3. Provision de cartera
    .to("direct:btCalcularProvisiones")

    // 4. Generacion de reportes SBS
    .to("direct:generarReportesSBS")

    // 5. Cierre contable
    .to("direct:btCierreContable")

    .log("Cierre de dia completado");

// Proceso de desembolsos masivos
from("file:/data/desembolsos?fileName=lote-*.csv&move=.procesados")
    .routeId("desembolsosMasivos")
    .unmarshal().csv()
    .split(body()).streaming()
        .bean(desembolsoService, "procesarLinea")
        .to("direct:btCrearPrestamo")
    .end()
    .log("Lote de desembolsos procesado");
```

---

## 6. Regulaciones y reportes

### Reportes a SBS (Peru)

| Reporte | Frecuencia | Contenido |
|---------|------------|-----------|
| **Anexo 6** | Mensual | Detalle de cartera de creditos |
| **Anexo 5** | Mensual | Depositos y obligaciones |
| **RCD** | Mensual | Reporte Crediticio de Deudores |
| **Reporte de Operaciones Sospechosas** | Evento | Lavado de activos |
| **ITF** | Diario | Impuesto a las Transacciones Financieras |

```java
// Generacion automatica de reportes regulatorios
from("cron:reporteSBS?schedule=0+0+3+1+*+?") // Dia 1 de cada mes a las 3am
    .routeId("generarReporteSBS")
    .log("Generando Anexo 6 para SBS")
    .to("direct:btExtraerCartera")
    .process(exchange -> {
        List<Credito> cartera = exchange.getIn().getBody(List.class);
        // Transformar al formato requerido por SBS
        String reporteSBS = reporteService.generarAnexo6(cartera);
        exchange.getIn().setBody(reporteSBS);
    })
    .to("file:/data/reportes/sbs?fileName=anexo6-${date:now:yyyyMM}.txt")
    .to("sftp://sbs-upload.sbs.gob.pe/reportes"
        + "?username={{sbs.user}}&privateKeyFile=/certs/sbs-key.pem");
```

---

## 7. Conceptos de riesgo crediticio

| Concepto | Descripcion |
|----------|-------------|
| **Score crediticio** | Puntaje que mide la probabilidad de pago del cliente |
| **Central de riesgo** | Entidad que registra historial crediticio (Sentinel, Equifax) |
| **Clasificacion de riesgo** | Normal, CPP, Deficiente, Dudoso, Perdida |
| **Provision** | Reserva contable segun clasificacion de riesgo |
| **Capacidad de pago** | Ingreso neto disponible para pagar cuotas |
| **Garantia** | Bien que respalda el credito (hipoteca, prenda) |
| **Ratio de morosidad** | Cartera morosa / cartera total |
| **Sobreendeudamiento** | Cliente con obligaciones que superan su capacidad de pago |

---

## 8. Glosario financiero para entrevistas

| Termino | Definicion rapida |
|---------|-------------------|
| **Amortizacion** | Pago del capital del prestamo |
| **Cronograma de pagos** | Tabla con fechas, cuotas, capital, interes, saldo |
| **Desembolso** | Entrega del dinero del prestamo al cliente |
| **Prepago** | Pago anticipado del prestamo (parcial o total) |
| **Refinanciamiento** | Reestructurar condiciones de un prestamo existente |
| **Castigo** | Eliminar contablemente un credito irrecuperable |
| **Colocacion** | Prestamos otorgados (cartera activa) |
| **Captacion** | Depositos recibidos de clientes |
| **Liquidez** | Capacidad de la entidad para cumplir obligaciones inmediatas |
| **Patrimonio** | Recursos propios de la entidad |
| **ROE** | Return on Equity: utilidad / patrimonio |
| **ROA** | Return on Assets: utilidad / activos totales |

---

## Preguntas de entrevista

### Conocimiento del sector
1. **Que es una caja municipal y en que se diferencia de un banco?**
   - Caja municipal es una entidad financiera de propiedad municipal enfocada en microfinanzas (pymes y personas de bajos recursos). Tiene menor alcance pero mayor penetracion en segmentos no bancarizados
2. **Que es la SBS y por que es importante?**
   - Superintendencia de Banca, Seguros y AFP: regulador financiero peruano. Define normativa que las entidades deben cumplir (reportes, provisiones, ratios)
3. **Que diferencia hay entre TEA y TCEA?**
   - TEA solo incluye intereses. TCEA incluye intereses + comisiones + seguros + gastos (costo real para el cliente)
4. **Que es la mora y como se clasifica?**
   - Retraso en pago. Clasificacion SBS: Normal (0-8 dias), CPP (9-30), Deficiente (31-60), Dudoso (61-120), Perdida (+120)

### Rol del middleware
5. **Cual es el rol del middleware en una entidad financiera?**
   - Orquestar comunicacion entre canales digitales y core bancario, transformar datos, integrar sistemas externos, aplicar logica de negocio transversal
6. **Que procesos batch maneja tipicamente el middleware?**
   - Cierre de dia, calculo de intereses, actualizacion de mora, generacion de reportes regulatorios, desembolsos masivos
7. **Como integrarias un nuevo canal digital (app movil)?**
   - API Gateway para autenticacion, rutas Camel para orquestar operaciones con BT, transformacion de formato, validaciones de negocio

### Tecnicas con contexto financiero
8. **Como garantizas la consistencia en una transferencia entre cuentas?**
   - Transaccion XA o Saga: debito + credito atomico. Si falla el credito, reversar el debito. Idempotencia para evitar duplicados
9. **Como manejas picos de trafico (fin de mes, quincena)?**
   - HPA en OpenShift, colas JMS como buffer, throttling, circuit breaker para proteger el core
10. **Como implementas el proceso de evaluacion crediticia?**
    - Orquestacion: validar identidad (RENIEC) → consultar central de riesgo (Sentinel) → evaluar capacidad de pago → simular en BT → decision

---

## Tabla de respuestas rapidas

| Pregunta | Respuesta corta |
|----------|----------------|
| Core bancario? | Software central que gestiona operaciones del banco |
| Caja municipal? | Entidad financiera municipal enfocada en microfinanzas |
| SBS? | Regulador financiero peruano |
| TEA vs TCEA? | TEA=solo intereses, TCEA=costo total (intereses+comisiones+seguros) |
| Mora? | Retraso en pago. 5 clasificaciones SBS |
| Provision? | Reserva contable para cubrir creditos morosos |
| Rol del middleware? | Orquestar canales-core, transformar, integrar, validar |
| Bantotal? | Core bancario mas usado en LATAM |
| Score crediticio? | Puntaje que mide probabilidad de pago |
| Proceso batch? | Cierre de dia, calculo intereses, reportes SBS |

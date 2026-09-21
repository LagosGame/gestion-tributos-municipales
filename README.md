# Gestión de Tributos Municipales — API REST

API REST para la gestión de tributos municipales (IBI, tasas, IVTM, multas de
tráfico...): altas de contribuyentes e inmuebles, emisión y consulta de
recibos, registro de pagos, y **cálculo automático de recargos por impago
según el artículo 28 de la Ley General Tributaria (Ley 58/2003)**.

## Por qué este proyecto

Lo hice pensando específicamente en el tipo de sistema que gestiona una
empresa de gestión tributaria para administraciones públicas. Quería
demostrar que no solo sé construir un CRUD, sino que entiendo el dominio de
negocio: periodo voluntario vs. ejecutivo, providencia de apremio, y los
tres tramos de recargo (5% / 10% / 20% + intereses de demora) que marca la
ley.

## Stack

- Java 21 · Spring Boot 4 · Spring Data JPA
- PostgreSQL (vía Docker) · Hibernate
- Docker / Docker Compose
- Lombok · Bean Validation (Jakarta Validation)
- Documentación interactiva con Swagger / OpenAPI 

## Modelo de dominio

```
Contribuyente 1---N Inmueble
Contribuyente 1---N Recibo
Recibo        1---N Pago
Recibo        1---N Recargo
```

Un **Recibo** nace en estado `PENDIENTE` / periodo `VOLUNTARIO`. Si no se
paga antes de `fechaVencimiento`, pasa a `EN_EJECUTIVA`. Si se notifica una
providencia de apremio y tampoco se paga en plazo, el recargo pasa de un 5%
(ejecutivo) a un 10% (apremio reducido) o un 20% + intereses de demora
(apremio ordinario), calculado automáticamente en `RecargoService` en el
momento de registrar el pago.

## Estructura del proyecto

```
src/main/java/com/gestion_municipal/gestion_municipal/
├── entidades/     → las 5 entidades JPA + sus enums (TipoTributo, EstadoRecibo...)
├── repository/     → interfaces JpaRepository con consultas derivadas
├── service/        → lógica de negocio, incluido RecargoService (LGT)
├── controllers/    → los 4 controladores REST
└── exceptions/      → excepciones personalizadas + manejador global de errores
```

## Cómo arrancarlo

**1. Levanta la base de datos con Docker:**
```bash
docker compose up -d
```
(usa el puerto `5433` en el host para evitar conflictos con un PostgreSQL
nativo que pueda estar instalado en el sistema — ver `docker-compose.yml`)

**2. Arranca la aplicación** desde tu IDE (clase `GestionMunicipalApplication`) o con:
```bash
./mvnw spring-boot:run
```

**3. Abre la documentación interactiva:**
```
http://localhost:8080/swagger-ui.html
```

## Flujo probado de extremo a extremo

Este proyecto no se quedó solo en "compila" — se probó el ciclo de negocio
completo a través de Swagger:

1. `POST /api/contribuyentes` → alta de un contribuyente con su inmueble asociado (relación en cascada)
2. `POST /api/contribuyentes/{id}/recibos` → emisión de un recibo de IBI con vencimiento ya pasado
3. `POST /api/recibos/{id}/pagos` → registro de un pago fuera de plazo
4. `GET /api/recibos/{id}` → confirma que el recibo pasó a `PAGADO` **y** que se calculó automáticamente un recargo `EJECUTIVO` del 5%, con el importe exacto

## Endpoints principales

| Método | Endpoint                                          | Descripción                          |
|--------|----------------------------------------------------|----------------------------------------|
| POST   | `/api/contribuyentes`                              | Alta de contribuyente                 |
| GET    | `/api/contribuyentes/{id}`                          | Consulta de un contribuyente          |
| POST   | `/api/contribuyentes/{id}/inmuebles`                | Alta de inmueble                      |
| POST   | `/api/contribuyentes/{id}/recibos`                  | Emisión de un recibo                  |
| GET    | `/api/recibos/{id}`                                 | Consulta de un recibo (con pagos y recargos) |
| POST   | `/api/recibos/procesar-vencidos`                    | Pasa a ejecutiva los recibos vencidos |
| POST   | `/api/recibos/{id}/notificar-apremio?fecha=...`     | Notifica providencia de apremio       |
| POST   | `/api/recibos/{reciboId}/pagos`                     | Registra un pago (calcula recargo automáticamente si aplica) |

## Tests

`RecargoServiceTest` cubre los cuatro escenarios de la LGT con JUnit 5 y
Mockito: pago en periodo voluntario (sin recargo), recargo ejecutivo,
apremio reducido, y apremio ordinario con intereses de demora calculados
día a día.

## Próximos pasos

Frontend en React consumiendo esta API — en desarrollo.

## Autor

Germán Cabrera Alemán — Backend Developer Java | Spring Boot
[github.com/LagosGame](https://github.com/LagosGame)

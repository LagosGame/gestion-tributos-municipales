# Gestión de Tributos Municipales

Aplicación fullstack para la gestión de tributos municipales (IBI, tasas,
IVTM, multas de tráfico...): altas de contribuyentes e inmuebles, emisión y
consulta de recibos, registro de pagos, y **cálculo automático de recargos
por impago según el artículo 28 de la Ley General Tributaria (Ley
58/2003)**.

## 🔗 Demo en producción

- **Aplicación (frontend):** https://gestion-municipal-frontend-nntf.vercel.app/
- **API (backend) + Swagger:** https://gestion-tributos-backend.onrender.com/swagger-ui.html

> El backend está en un plan gratuito que "duerme" tras un rato de
> inactividad — la primera petición tras un tiempo sin uso puede tardar
> hasta un minuto en responder mientras arranca de nuevo. Las siguientes
> son instantáneas.

## Por qué este proyecto

Lo hice pensando específicamente en el tipo de sistema que gestiona una
empresa de gestión tributaria para administraciones públicas. Quería
demostrar que no solo sé construir un CRUD, sino que entiendo el dominio de
negocio: periodo voluntario vs. ejecutivo, providencia de apremio, y los
tres tramos de recargo (5% / 10% / 20% + intereses de demora) que marca la
ley — incluyendo una simulación previa que le dice al usuario cuánto debe
pagar exactamente antes de confirmar el pago, en vez de obligarle a
calcularlo él mismo.

## Stack

**Backend**
- Java 21 · Spring Boot 4 · Spring Data JPA · Hibernate
- PostgreSQL · Docker
- Lombok · Bean Validation (Jakarta Validation)
- Swagger / OpenAPI
- JUnit 5 + Mockito

**Frontend**
- React 18 + Vite
- CSS propio (sin frameworks de componentes)
- Consumo de la API vía `fetch`, con variables de entorno por entorno (dev/prod)

**Despliegue**
- Backend → [Render](https://render.com) (contenedor Docker)
- Base de datos → [Neon](https://neon.tech) (PostgreSQL serverless)
- Frontend → [Vercel](https://vercel.com)

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
(apremio ordinario). El cálculo vive en `RecargoService`, tanto para
guardarlo al registrar un pago real como para simularlo sin guardar nada
(el aviso que ve el usuario antes de pagar).

## Estructura del backend

```
src/main/java/com/gestion_municipal/gestion_municipal/
├── entidades/    → las 5 entidades JPA + sus enums (TipoTributo, EstadoRecibo...)
├── repository/   → interfaces JpaRepository con consultas derivadas
├── service/      → lógica de negocio, incluido RecargoService (LGT)
├── controllers/  → los 4 controladores REST
└── exceptions/   → excepciones personalizadas + manejador global de errores
```

## Estructura del frontend

```
src/
├── components/   → FormularioContribuyente, TablaContribuyentes,
│                   FormularioRecibo, TablaRecibos, DetalleRecibo, FormularioPago
├── utils/        → helpers compartidos (ej. clases de estado/badges)
├── App.jsx       → estado global y orquestación de los componentes
└── App.css       → estilos
```

## Cómo arrancarlo en local

**Backend**
```bash
docker compose up -d          # levanta PostgreSQL
./mvnw spring-boot:run        # o Run desde el IDE
```
Documentación interactiva en `http://localhost:8080/swagger-ui.html`

**Frontend**
```bash
npm install
npm run dev
```
Disponible en `http://localhost:5173`. Configura `VITE_API_URL` en un
archivo `.env` (ver `.env.example`) apuntando a tu backend local.

## Flujo probado de extremo a extremo

1. `POST /api/contribuyentes` → alta de un contribuyente con su inmueble asociado (relación en cascada)
2. `POST /api/contribuyentes/{id}/recibos` → emisión de un recibo de IBI
3. `GET /api/recibos/{id}/simulacion-pago?fecha=...` → previsualiza cuánto habría que pagar en esa fecha, sin guardar nada
4. `POST /api/recibos/{id}/pagos` → registra el pago, calculando y persistiendo el recargo si corresponde
5. `GET /api/recibos/{id}` → confirma que el recibo pasó a `PAGADO` y que el recargo aplicado coincide con el simulado

Probado tanto contra la base de datos local como contra la instancia real
en Neon, en el entorno desplegado.

## Endpoints principales

| Método | Endpoint                                          | Descripción                          |
|--------|----------------------------------------------------|----------------------------------------|
| POST   | `/api/contribuyentes`                              | Alta de contribuyente                 |
| GET    | `/api/contribuyentes/{id}`                          | Consulta de un contribuyente          |
| POST   | `/api/contribuyentes/{id}/inmuebles`                | Alta de inmueble                      |
| POST   | `/api/contribuyentes/{id}/recibos`                  | Emisión de un recibo                  |
| GET    | `/api/recibos/{id}`                                 | Consulta de un recibo (con pagos y recargos) |
| GET    | `/api/recibos/{id}/simulacion-pago?fecha=...`       | Simula el importe a pagar sin guardar nada |
| POST   | `/api/recibos/procesar-vencidos`                    | Pasa a ejecutiva los recibos vencidos |
| POST   | `/api/recibos/{id}/notificar-apremio?fecha=...`     | Notifica providencia de apremio       |
| POST   | `/api/recibos/{reciboId}/pagos`                     | Registra un pago (calcula recargo automáticamente si aplica) |

## Tests

`RecargoServiceTest` cubre los cuatro escenarios de la LGT con JUnit 5 y
Mockito: pago en periodo voluntario (sin recargo), recargo ejecutivo,
apremio reducido, y apremio ordinario con intereses de demora calculados
día a día.

## Repositorios

- Backend: [github.com/LagosGame/gestion_municipal](https://github.com/LagosGame/gestion_municipal)
- Frontend: [github.com/LagosGame/gestion-municipal-frontend](https://github.com/LagosGame/gestion-municipal-frontend)

## Autor

Germán Cabrera Alemán — Backend Developer Java | Spring Boot
[github.com/LagosGame](https://github.com/LagosGame)
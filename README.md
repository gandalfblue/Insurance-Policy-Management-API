# Insurance Policy Management API

API REST para administrar clientes, pólizas y riesgos, con auditoría de cambios e integración con un sistema CORE. El proyecto usa Java 21, Spring Boot, virtual threads y Springdoc OpenAPI.

> Proyecto de demostración. El API key de desarrollo es público y solo sirve para ejecutar pruebas locales. No lo uses en un despliegue real.

## Contenido

- [Requisitos y ejecución](#requisitos-y-ejecución)
- [Swagger y autenticación](#swagger-y-autenticación)
- [Flujo recomendado](#flujo-recomendado)
- [Recursos y operaciones](#recursos-y-operaciones)
- [Tipos y valores permitidos](#tipos-y-valores-permitidos)
- [Paginación y filtros](#paginación-y-filtros)
- [Ejemplos de solicitudes](#ejemplos-de-solicitudes)
- [Respuestas y errores](#respuestas-y-errores)
- [Perfiles y configuración](#perfiles-y-configuración)
- [Ejecución de pruebas](#ejecución-de-pruebas)
- [Seguridad para despliegues](#seguridad-para-despliegues)

## Requisitos y ejecución

- JDK 21.
- Maven 3.9 o superior, o el Maven Wrapper incluido.

Inicia el servicio desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

La aplicación inicia en `http://localhost:8080` y usa H2 en memoria con el perfil `development` por defecto. Al reiniciar, los datos de desarrollo se pierden.

### Virtual threads

Virtual threads están habilitados en los perfiles `development`, `test` y `prod` mediante `spring.threads.virtual.enabled`. Spring Boot los usa para el servidor web integrado y sus ejecutores compatibles. Se requiere Java 21 o posterior. `spring.main.keep-alive` también está habilitado para que la JVM permanezca activa junto con el procesador programado de reintentos de CORE. Esta configuración mejora la capacidad de concurrencia en tareas de espera de I/O; no aumenta por sí sola el rendimiento de CPU ni elimina límites de conexiones a la base de datos o a servicios externos.

## Swagger y autenticación

- Interfaz interactiva: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Documento OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- Salud del servicio: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

Todas las rutas bajo `/api/**` requieren el encabezado `x-api-key`. La clave local incluida para desarrollo es `test-api-key`.

1. En Swagger, pulsa **Authorize**.
2. Escribe `test-api-key` en el campo de API key y confirma.
3. Ejecuta la operación deseada. Swagger adjuntará el encabezado automáticamente.

También puedes enviar el encabezado manualmente desde un cliente HTTP:

```http
x-api-key: test-api-key
```

La UI de Swagger, el documento OpenAPI y el endpoint de salud son públicos en la configuración local. Si recibes `401 Missing x-api-key header`, autoriza Swagger o agrega el encabezado a la solicitud.

## Flujo recomendado

Para crear una póliza desde cero:

1. Crea un cliente con `POST /api/customers` y guarda el `customerId` de la respuesta.
2. Crea uno o más riesgos con `POST /api/risks` y guarda sus `riskId`.
3. Crea la póliza con `POST /api/policies`, usando el `customerId` y, si aplica, los `riskIds` existentes.
4. Consulta o actualiza los recursos mediante sus identificadores UUID.

La póliza se crea con estado `ACTIVE`. Los riesgos se crean con estado `ACTIVE`. El sistema genera los UUID y las fechas de auditoría.

## Recursos y operaciones

Todas las rutas de esta sección requieren `x-api-key`.

### Clientes — `/api/customers`

| Método | Ruta | Acción | Respuesta habitual |
| --- | --- | --- | --- |
| `POST` | `/api/customers` | Crear cliente | `201 Created` |
| `GET` | `/api/customers/{customerId}` | Consultar cliente por UUID | `200 OK` |
| `PUT` | `/api/customers/{customerId}` | Actualizar cliente | `200 OK` |

El correo y los nombres son obligatorios. El correo debe tener formato válido; teléfono es opcional.

### Pólizas — `/api/policies`

| Método | Ruta | Acción | Respuesta habitual |
| --- | --- | --- | --- |
| `POST` | `/api/policies` | Crear póliza | `201 Created` |
| `GET` | `/api/policies/{policyId}` | Consultar póliza por UUID | `200 OK` |
| `GET` | `/api/policies` | Listar pólizas con filtros y paginación | `200 OK` |
| `PUT` | `/api/policies/{policyId}` | Actualizar póliza | `200 OK` |
| `DELETE` | `/api/policies/{policyId}` | Eliminar póliza | `204 No Content` |

Para crear una póliza se necesita un `customerId` existente, un tipo admitido, montos positivos y fechas válidas. `riskIds` es opcional; los riesgos asociados deben existir.

### Riesgos — `/api/risks`

| Método | Ruta | Acción | Respuesta habitual |
| --- | --- | --- | --- |
| `POST` | `/api/risks` | Crear riesgo | `201 Created` |
| `GET` | `/api/risks/{riskId}` | Consultar riesgo por UUID | `200 OK` |
| `GET` | `/api/risks` | Listar riesgos con filtros y paginación | `200 OK` |
| `PUT` | `/api/risks/{riskId}` | Actualizar riesgo | `200 OK` |
| `DELETE` | `/api/risks/{riskId}` | Eliminar riesgo | `204 No Content` |

El nombre del riesgo debe ser único. Nombre, categoría, severidad y descripción son obligatorios al crearlo; su estado inicial es `ACTIVE`.

### Integración CORE — `/api/core`

| Método | Ruta | Acción | Respuesta habitual |
| --- | --- | --- | --- |
| `POST` | `/api/core/sync` | Solicitar sincronización de pólizas, riesgos o ambos | `200 OK` |
| `GET` | `/api/core/query` | Consultar el sistema CORE | `200 OK` |

El `POST` puede recibir `syncType` con `POLICIES`, `RISKS` o `ALL`. Si el cuerpo se omite, se usa `ALL`. La consulta requiere `type`; acepta `customerId` y `policyStatus` como criterios opcionales. En desarrollo, la URL del adaptador CORE es `http://localhost:8080` salvo que se configure `CORE_BASE_URL`.

## Tipos y valores permitidos

### Tipos de póliza

`policyType` acepta estos valores (se normalizan a mayúsculas):

- `AUTO`
- `HOME`
- `HEALTH`
- `LIFE`

### Categorías de riesgo

`riskCategory` debe ser uno de estos valores exactos:

`NATURAL_DISASTER`, `ACCIDENT`, `THEFT`, `FIRE`, `FLOOD`, `EARTHQUAKE`, `WINDSTORM`, `EXPLOSION`, `RIOT`, `VANDALISM`.

### Severidad del riesgo

`riskSeverity`: `HIGH`, `MEDIUM` o `LOW`.

### Estado del riesgo

`status`: `ACTIVE`, `INACTIVE` o `MONITORED`. En la creación el servicio asigna `ACTIVE`.

### Estado de la póliza

`status`: `ACTIVE`, `INACTIVE`, `SUSPENDED` o `CANCELLED`. En la creación el servicio asigna `ACTIVE`.

### Tipo de sincronización CORE

`syncType`: `POLICIES`, `RISKS` o `ALL`.

## Paginación y filtros

Los listados de pólizas y riesgos aceptan `pageNumber` (empieza en **1**, predeterminado `1`) y `pageSize` (predeterminado `10`, máximo `100`). Por ejemplo, para la segunda página de 20 resultados: `?pageNumber=2&pageSize=20`.

Filtros de pólizas:

- `customerId`: UUID del cliente.
- `status`: estado de póliza permitido.

Filtros de riesgos:

- `riskCategory`: categoría permitida.
- `riskSeverity`: severidad permitida.
- `status`: estado de riesgo permitido.

Los listados devuelven un objeto paginado con los resultados y metadatos de paginación. La respuesta de cada recurso contiene su UUID; úsalo para las operaciones siguientes.

## Ejemplos de solicitudes

Puedes copiar estos JSON en el cuerpo de Swagger usando `Try it out`. Las fechas deben estar en formato ISO `YYYY-MM-DD`. Reemplaza los UUID de ejemplo por los valores devueltos en tu entorno.

### Crear un cliente

`POST /api/customers`

```json
{
  "email": "ana.garcia@example.com",
  "firstName": "Ana",
  "lastName": "García",
  "phone": "+57 300 123 4567"
}
```

### Crear un riesgo

`POST /api/risks`

```json
{
  "riskName": "Inundación en zona residencial",
  "riskCategory": "FLOOD",
  "riskSeverity": "MEDIUM",
  "description": "Posible afectación por lluvias intensas y desbordamiento.",
  "status": "ACTIVE"
}
```

El estado enviado al crear un riesgo se inicializa como `ACTIVE` por el servicio.

### Crear una póliza

`POST /api/policies`

```json
{
  "customerId": "11111111-1111-1111-1111-111111111111",
  "policyType": "HOME",
  "coverageAmount": 250000000,
  "premiumAmount": 1250000,
  "startDate": "2030-01-01",
  "endDate": "2030-12-31",
  "description": "Póliza de hogar de ejemplo",
  "riskIds": [
    "22222222-2222-2222-2222-222222222222"
  ]
}
```

Usa un `customerId` creado previamente y `riskIds` existentes; los identificadores ficticios de arriba son solo ilustrativos. `endDate`, `description` y `riskIds` pueden omitirse. `startDate` debe ser hoy o una fecha futura; `endDate`, cuando se especifica, debe ser hoy o futuro y no anterior a `startDate`. Los importes deben ser mayores que cero.

### Actualizar una póliza

`PUT /api/policies/{policyId}` aplica los campos no nulos del cuerpo. Por ejemplo:

```json
{
  "status": "SUSPENDED",
  "description": "Póliza suspendida temporalmente"
}
```

Consulta en Swagger el esquema de cada operación para ver los campos permitidos en los demás cuerpos de actualización.

### Sincronizar con CORE

`POST /api/core/sync`

```json
{
  "syncType": "ALL",
  "timeoutSeconds": 30
}
```

## Respuestas y errores

Los errores usan una estructura común con campos como `errorCode`, `errorMessage`, `timestamp`, `path`, `traceId` y `fieldErrors` (los campos opcionales pueden ser `null`). Estados HTTP habituales:

| Estado | Significado |
| --- | --- |
| `200` | Consulta o actualización completada |
| `201` | Recurso creado; la respuesta incluye el recurso y su UUID |
| `204` | Eliminación completada sin cuerpo de respuesta |
| `400` | Solicitud inválida, validación fallida o valor de filtro no permitido |
| `401` | Falta `x-api-key` o la clave no es válida |
| `404` | No se encontró el recurso solicitado |
| `5xx` | Error interno o fallo de integración con CORE |

Si recibes `400`, revisa los nombres de campos, formatos, enums, fechas, importes y relaciones UUID. Si recibes `401`, vuelve a autorizar Swagger con la clave correspondiente al entorno.

## Perfiles y configuración

- `development` (predeterminado): H2 en memoria y clave local de desarrollo.
- `test`: configuración aislada para pruebas.
- `prod`: PostgreSQL y API key inyectada mediante variable de entorno.

Variables usadas en el perfil de producción:

| Variable | Uso |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | Selecciona `prod` |
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario de base de datos |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de base de datos |
| `API_KEY` | Clave válida para el encabezado `x-api-key` |
| `CORE_BASE_URL` | URL base del servicio CORE |

Ejemplo local (reemplaza los valores por los de tu entorno):

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/insurance_db'
export SPRING_DATASOURCE_USERNAME='postgres'
export SPRING_DATASOURCE_PASSWORD='cambia-este-valor'
export API_KEY='genera-una-clave-segura'
export CORE_BASE_URL='http://localhost:8081'
./mvnw spring-boot:run
```

No subas archivos `.env`, contraseñas, claves reales, volcados de base de datos ni tokens al repositorio. Configura los secretos en el entorno de ejecución o en el gestor de secretos de tu plataforma.

## Ejecución de pruebas

```bash
./mvnw test
```

Para compilar, ejecutar pruebas y verificaciones del ciclo Maven:

```bash
./mvnw verify
```

## Seguridad para despliegues

El API key fijo de desarrollo facilita probar Swagger localmente y **no protege un despliegue público**. Para publicar el servicio, usa una clave única, aleatoria y privada; transmítela mediante HTTPS y gestiona su rotación fuera del código fuente. No uses el perfil `development` en producción. Evalúa además autenticación con identidades y permisos por consumidor, límites de tráfico, CORS restringido, copias de seguridad y monitorización.

## Licencia

Agrega aquí la licencia del proyecto antes de distribuirlo públicamente, si corresponde.

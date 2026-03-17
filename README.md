# Spring Boot 4 API Template

Reusable Spring Boot 4.0.3 + Java 25 starter for building production-ready REST APIs. Includes a clean package structure, consistent response format, profiles, security baseline, OpenAPI docs, and sample CRUD.

## Features

- Spring Boot 4.0.3, Java 25
- Standard layered architecture: controller -> service -> repository
- Consistent API response format
- Validation + global exception handling
- API docs with SpringDoc OpenAPI
- Profiles: `dev`, `test`, `prod`
- Security baseline: API key (default) or JWT (ready)
- Request correlation IDs + structured console logging
- Sample CRUD (Items)

## Requirements

- JDK 25+
- Maven (wrapper included)
- PostgreSQL 17+ (dev/prod)
- Docker (for tests via Testcontainers)

## Quick Start (Dev)

1. Configure a local PostgreSQL database:

```
DB_URL=jdbc:postgresql://localhost:5432/template_dev
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

2. Run:

```bash
./scripts/run-local.sh
```

Windows:

```powershell
.\scripts\run-local.ps1
```

Or:

```bash
./mvnw spring-boot:run
```

## Profiles

- `dev`: local development defaults, SQL logging enabled
- `test`: PostgreSQL via Testcontainers, Flyway enabled
- `prod`: production defaults, security enabled

Activate a profile:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

## Base URL

Default app URL: `http://localhost:8080`  
Base path: `/api/v1`

## Swagger and OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## API Endpoints (Full URLs)

- `GET http://localhost:8080/api/v1/health`
- `GET http://localhost:8080/api/v1/info`
- `POST http://localhost:8080/api/v1/items`
- `GET http://localhost:8080/api/v1/items/{id}`
- `GET http://localhost:8080/api/v1/items`
- `PUT http://localhost:8080/api/v1/items/{id}`
- `DELETE http://localhost:8080/api/v1/items/{id}`

## Sample Requests

Create item:

```bash
curl -X POST http://localhost:8080/api/v1/items \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Sample Item\",\"description\":\"Created via curl\"}"
```

Get item:

```bash
curl http://localhost:8080/api/v1/items/1
```

## Response Format

```json
{
  "timestamp": "2026-03-17T12:34:56.789Z",
  "path": "/api/v1/items/1",
  "requestId": "c8fdd5dd-1e7b-4f1e-9f4f-8a6e9f9c1a7c",
  "status": 200,
  "data": {
    "id": 1,
    "name": "Sample Item",
    "description": "Created via test",
    "status": "ACTIVE",
    "createdAt": "2026-03-17T12:34:56.789Z",
    "updatedAt": "2026-03-17T12:34:56.789Z"
  }
}
```

Errors:

```json
{
  "timestamp": "2026-03-17T12:34:56.789Z",
  "path": "/api/v1/items/999",
  "requestId": "c8fdd5dd-1e7b-4f1e-9f4f-8a6e9f9c1a7c",
  "status": 404,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Item not found: 999"
  }
}
```

## Security

Enable API key auth (default in `prod`):

```
SECURITY_ENABLED=true
API_KEY_ENABLED=true
API_KEY_HEADER=X-API-KEY
API_KEY_VALUE=change-me
```

Example call with API key:

```bash
curl -H "X-API-KEY: change-me" http://localhost:8080/api/v1/items
```

Enable JWT:

```
SECURITY_ENABLED=true
API_KEY_ENABLED=false
JWT_ENABLED=true
JWT_ISSUER_URI=https://issuer.example.com/realms/demo
```

JWT without issuer (local/dev options):

Option A: JWKS endpoint:

```
SECURITY_ENABLED=true
API_KEY_ENABLED=false
JWT_ENABLED=true
JWT_JWK_SET_URI=https://issuer.example.com/realms/demo/protocol/openid-connect/certs
```

Option B: HMAC secret (HS256, dev only):

```
SECURITY_ENABLED=true
API_KEY_ENABLED=false
JWT_ENABLED=true
JWT_HMAC_SECRET=dev-secret-1234567890-1234567890-1234
```

Generate a dev token (PowerShell):

```powershell
.\scripts\jwt-hs256.ps1 -Secret "dev-secret-1234567890-1234567890-1234" -Subject "user1" -TtlMinutes 60
```

Note: HS256 requires a secret length of at least 32 characters (256 bits). Shorter secrets will fail.

When JWT is enabled, call APIs with:

```
Authorization: Bearer <jwt-token>
```

Production recommendation: use `JWT_ISSUER_URI` or `JWT_JWK_SET_URI` (RS256/ES256). Avoid HMAC in production.

Note: Set exactly one of `JWT_ISSUER_URI`, `JWT_JWK_SET_URI`, or `JWT_HMAC_SECRET`.

## Actuator

- Base URL: `http://localhost:9000/actuator`
- Exposed by default: `health`, `info`, `metrics`, `prometheus`

## Project Structure

```
src/
  main/
    java/com/demo/
      config/          # App properties, security, CORS, OpenAPI
      controller/      # REST controllers
      dto/             # Request/response DTOs
      entity/          # JPA entities
      exception/       # Custom exceptions + handler
      filter/          # Correlation ID + request logging
      repository/      # Spring Data repositories
      security/        # API key authentication
      service/         # Business logic
      util/            # Mappers + response helpers
    resources/
      application.yml
      application-dev.yml
      application-test.yml
      application-prod.yml
      db/migration/
  test/
    java/com/demo/
```

## Tests

Tests use [Testcontainers](https://testcontainers.com/) to spin up a real PostgreSQL instance in Docker. This ensures tests run against the same database engine as production -- no H2 dialect mismatches.

**Requirements:** Docker must be running.

```bash
./mvnw test
```

All integration tests extend `AbstractIntegrationTest`, which starts a shared PostgreSQL 17 container and injects the datasource properties via `@DynamicPropertySource`. Flyway migrations run automatically against the container.

## Build and Run

```bash
./mvnw clean package
java -jar target/template-api.jar
```

Docker:

```bash
docker build -t template-api .
docker run -p 8080:8080 template-api
```

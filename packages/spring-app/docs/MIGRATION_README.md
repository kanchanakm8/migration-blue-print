# Migration demo: Spring Boot -> Node.js

This repository contains a compact Spring Boot example designed to highlight common elements you will migrate to Node.js: controllers (REST), services, persistence (JPA), DTO validation, global error handling, tests, OpenAPI, sample data, and a Dockerfile.

Why this example?
- REST endpoints with CRUD semantics (`/api/products`).
- Service layer with business logic.
- JPA-based persistence (H2) and repository patterns.
- DTOs with validation (`jakarta.validation`).
- Global exception handling and structured errors.
- Unit tests using `MockMvc`.
- OpenAPI spec and sample `data.sql`.

How this maps to Node.js migration tasks
- Controller -> Express/Koa route handlers
- Service -> service modules (separate business logic)
- Repository (JPA) -> ORM layer (TypeORM/Sequelize/Prisma) or raw queries
- DTO validation -> `class-validator` or `Joi`
- Global exception handling -> centralized error middleware
- Tests -> Jest + Supertest
- OpenAPI -> reuse `openapi.yaml` and generate client/server stubs

Workflow for demo (suggested):
1. Run Spring app locally: `./mvnw spring-boot:run` (requires JDK + Maven or `./mvnw`).
2. Explore endpoints: GET `/api/products`, POST `/api/products`.
3. Show `openapi.yaml` and explain mapping to Express routes.
4. Scaffold a Node.js project and port one endpoint (e.g., `GET /api/products`) showing code side-by-side.
5. Repeat for validation, persistence, and tests.

Files of interest:
- `src/main/java/com/example/springapp/controller/ProductController.java`
- `src/main/java/com/example/springapp/service/ProductService.java`
- `src/main/java/com/example/springapp/repository/ProductRepository.java`
- `src/main/java/com/example/springapp/dto/ProductDto.java`
- `src/test/java/com/example/springapp/controller/ProductControllerTest.java`
- `openapi.yaml`, `Dockerfile`, `src/main/resources/data.sql`

Presentation suggestions:
- Before/after code snippets for one endpoint (controller + service + repo).
- Time estimates for each migration step.
- Short live demo building the same endpoint in Node.js using Express and Prisma/SQLite.

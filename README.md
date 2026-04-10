# Lending Application

## Tech Stack
- Spring Boot 4.0.5 + Java 25
- PostgreSQL + Flyway migrations
- Modular package structure (api + internal per module)
- Dockerized (app + DB)

## Quick Start
1. `docker compose up --build`
2. App available at http://localhost:8080
3. Swagger available at http://localhost:8080/swagger-ui/index.html

## API Examples
POST /api/products → create product with tenure + fees  
POST /api/customers → create customer with limit  
POST /api/loans → disburse loan (validates limit)  
POST /api/loans/{id}/repay → repayment + state update  
Sweep job runs automatically.

## Architecture
- DDD-style modules with public `api` interfaces for inter-module communication.
- Autonomous DB schema via Flyway (V1 schema + V2 seed data).
- Event-driven notifications + scheduled sweep jobs.
- Full RESTful design + JPA entities.

Run tests: `./mvnw clean test -Dspring.profiles.active=test`
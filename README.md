# Order Processing API

[![CI](https://github.com/leonlimask20-dot/order-processing-api/actions/workflows/ci.yml/badge.svg)](https://github.com/leonlimask20-dot/order-processing-api/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8-red?logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)

REST API for order processing built with Java 17 + Spring Boot 3.5, applying
**Clean Architecture** (Ports & Adapters) to isolate the business domain from
frameworks, database and infrastructure details.

## Quick links

| | |
|---|---|
| Create order | `POST http://localhost:8080/api/v1/orders` |
| Get order | `GET http://localhost:8080/api/v1/orders/{id}` |
| Cancel order | `DELETE http://localhost:8080/api/v1/orders/{id}` |
| Run with Docker | [Go to section](#running) |

## Key skills demonstrated

- Clean Architecture with strict separation between domain, application and infrastructure
- Ports & Adapters: `OrderRepository` defined in the domain, implemented in the persistence layer
- Dependency Inversion: use cases are plain POJOs — no `@Service`, no Spring dependency
- Domain entities with invariants and encapsulated behavior
- Input and output DTOs kept separate from domain and JPA entities
- Persistence with Spring Data JPA and PostgreSQL
- Centralized error handling with `@RestControllerAdvice`
- Input validation with Bean Validation
- Containerization with Docker and Docker Compose

## Tech stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.13 |
| Spring Data JPA | 3.x |
| Hibernate | 6.6 |
| PostgreSQL | 16 |
| Lombok | — |
| Docker + Docker Compose | — |

## Order flow

```
1. POST /api/v1/orders        →  PlaceOrderRequest (input DTO)
2. OrderController            →  maps DTO to domain List<OrderItem>
3. PlaceOrderUseCase          →  creates Order, applies business rules
4. OrderRepository (port)     →  interface defined in the domain
5. OrderRepositoryImpl        →  implements the port using JPA
6. OrderResponseMapper        →  converts Order (domain) to OrderResponse (output DTO)
```

The domain never knows about JPA, Spring or HTTP. Dependencies always point inward.

## Architecture

```
src/main/java/com/lnl/orderprocessing/
├── domain/                    → Core. Zero external dependencies.
│   ├── entity/                │   Order, OrderItem, Customer
│   ├── enums/                 │   OrderStatus
│   ├── event/                 │   OrderPlacedEvent
│   └── repository/            │   OrderRepository (interface — outbound port)
│
├── application/               → Use cases. Orchestrates the domain.
│   └── usecase/               │   PlaceOrderUseCase, CancelOrderUseCase, TrackOrderUseCase
│                              │   Plain POJOs — no @Service, no Spring
│
├── adapters/                  → Translators between domain and the outside world
│   ├── controller/            │   OrderController, PlaceOrderRequest (HTTP input)
│   ├── presenter/             │   OrderResponseMapper, OrderResponse (HTTP output)
│   └── persistence/           │   OrderEntity (JPA), OrderRepositoryImpl (implements the port)
│
└── infrastructure/            → Configuration and error handling
    ├── config/                │   BeanConfig — wires use cases with Spring
    └── exception/             │   GlobalExceptionHandler
```

## Business rules

**Order lifecycle:**

```
PENDING → CONFIRMED → IN_PREPARATION → OUT_FOR_DELIVERY → DELIVERED
                                                              ↑
                                                    (cannot be cancelled)
```

- An order starts with status `PENDING`
- Only `PENDING` orders can be confirmed
- `DELIVERED` orders cannot be cancelled
- The total is calculated in the domain (`Order.total()`) — never in the database or controller
- Each `OrderItem` validates quantity > 0 and price > 0 on construction

**Design decisions:**

> **Why don't the use cases have `@Service`?**
> Annotating a use case with `@Service` creates a Spring dependency in the
> application core. Wiring is done explicitly in `BeanConfig`, keeping use
> cases as POJOs testable without a Spring context.

> **Why is `OrderEntity` separate from `Order`?**
> `Order` is the domain object, with behavior and invariants. `OrderEntity` is
> the relational representation with JPA annotations. Mixing them would couple
> the domain to the database.

> **Why is `SpringDataOrderRepository` package-private?**
> Only `OrderRepositoryImpl` can access the JPA repository. No other class can
> bypass the domain interface and hit the database directly.

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker Desktop

## Running

```bash
# Start the PostgreSQL database
docker-compose up -d

# Start the application
mvn spring-boot:run
```

API available at `http://localhost:8080`.

> **Note:** if you already have a PostgreSQL running on port 5432, Docker uses
> port 5433 in this project to avoid conflicts.

Hibernate creates the tables automatically on first run (`ddl-auto=update`).

## Endpoints

### Orders

| Method | Route | Description |
|---|---|---|
| `POST` | `/api/v1/orders` | Create order |
| `GET` | `/api/v1/orders/{id}` | Get order |
| `DELETE` | `/api/v1/orders/{id}` | Cancel order |

## Examples

### Create order

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-001",
    "items": [
      {
        "productId": "prod-001",
        "productName": "Cheeseburger",
        "quantity": 2,
        "unitPrice": 25.90
      }
    ]
  }'
```

```json
{
  "id": "f796cfd2-3413-4666-892e-f19c81a9c7c3",
  "customerId": "customer-001",
  "status": "PENDING",
  "total": 51.80,
  "items": [
    {
      "productId": "prod-001",
      "productName": "Cheeseburger",
      "quantity": 2,
      "unitPrice": 25.90,
      "subtotal": 51.80
    }
  ],
  "createdAt": "2026-04-11T18:03:04"
}
```

### Get order

```bash
curl http://localhost:8080/api/v1/orders/f796cfd2-3413-4666-892e-f19c81a9c7c3
```

### Cancel order

```bash
curl -X DELETE http://localhost:8080/api/v1/orders/f796cfd2-3413-4666-892e-f19c81a9c7c3
```

## 🤖 Agent Architecture

This project was built and code-reviewed using a **multi-agent
context-optimization workflow**: specialized AI agents each audit a single
architectural layer — domain, use cases, adapters, infrastructure, tests —
within a strict context budget. The approach cuts review time and token cost
while keeping full traceability of every finding.

Methodology, agent templates and the full playbook: **[leonlim3.gumroad.com](https://leonlim3.gumroad.com)**

## Production considerations

- Replace `ddl-auto=update` with migrations using Flyway or Liquibase
- Add integration tests with Testcontainers for parity with the production database
- Implement pagination on listing endpoints
- Add domain events published via RabbitMQ or Kafka when orders are confirmed
- Configure HTTPS with a TLS certificate

## Author

LNL &nbsp; GitHub: [@leonlimask20-dot](https://github.com/leonlimask20-dot) &nbsp; Email: leonlimask@gmail.com

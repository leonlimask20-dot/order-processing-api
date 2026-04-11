# Order Processing API

REST API de processamento de pedidos construída com **Java 17 + Spring Boot 3.5**, aplicando **Clean Architecture** (Ports & Adapters).

O objetivo do projeto é demonstrar como isolar o domínio de negócio de frameworks, banco de dados e detalhes de infraestrutura — permitindo que as regras de negócio sejam testadas sem nenhuma dependência externa.

---

## Arquitetura

```
src/main/java/com/lnl/orderprocessing/
│
├── domain/                        ← Núcleo. Zero dependências externas.
│   ├── entity/                    │   Order, OrderItem, Customer
│   ├── enums/                     │   OrderStatus
│   ├── event/                     │   OrderPlacedEvent
│   └── repository/                │   OrderRepository (interface — porta de saída)
│
├── application/                   ← Casos de uso. Orquestra o domínio.
│   └── usecase/                   │   PlaceOrderUseCase, CancelOrderUseCase, TrackOrderUseCase
│                                  │   POJOs puros — sem @Service, sem Spring
│
├── adapters/                      ← Tradutores entre o domínio e o mundo externo
│   ├── controller/                │   OrderController, PlaceOrderRequest (entrada HTTP)
│   ├── presenter/                 │   OrderResponseMapper, OrderResponse (saída HTTP)
│   └── persistence/               │   OrderEntity (JPA), OrderRepositoryImpl (implementa a porta)
│
└── infrastructure/                ← Configuração e tratamento de erros
    ├── config/                    │   BeanConfig — wiring dos use cases com Spring
    └── exception/                 │   GlobalExceptionHandler
```

### Por que essa separação importa

| Camada | Depende de | Não conhece |
|---|---|---|
| `domain` | nada | Spring, JPA, HTTP |
| `application` | `domain` | Spring, JPA, HTTP |
| `adapters` | `domain` + `application` | detalhes uns dos outros |
| `infrastructure` | tudo | — |

A interface `OrderRepository` é definida no `domain` e implementada em `adapters/persistence`. Isso significa que o domínio **nunca importa JPA** — a direção da dependência é invertida (Dependency Inversion Principle).

---

## Stack

- Java 17
- Spring Boot 3.5.13
- Spring Data JPA + Hibernate 6
- PostgreSQL 16
- Maven
- Docker / Docker Compose

---

## Como rodar localmente

**Pré-requisitos:** Java 17+, Maven, Docker

```bash
# 1. Sobe o banco
docker-compose up -d

# 2. Sobe a aplicação
mvn spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

> **Nota:** se você já tiver um PostgreSQL rodando na porta 5432, o Docker usa a porta 5433 por padrão neste projeto.

---

## Endpoints

### Criar pedido
```
POST /api/v1/orders
```
```json
{
  "customerId": "cliente-001",
  "items": [
    {
      "productId": "prod-001",
      "productName": "X-Burguer",
      "quantity": 2,
      "unitPrice": 25.90
    }
  ]
}
```

**Resposta `201 Created`:**
```json
{
  "id": "f796cfd2-3413-4666-892e-f19c81a9c7c3",
  "customerId": "cliente-001",
  "status": "PENDING",
  "total": 51.80,
  "items": [...],
  "createdAt": "2026-04-11T18:03:04"
}
```

### Consultar pedido
```
GET /api/v1/orders/{id}
```

### Cancelar pedido
```
DELETE /api/v1/orders/{id}
```

---

## Regras de negócio

- Um pedido nasce com status `PENDING`
- Só pedidos `PENDING` podem ser confirmados
- Pedidos `DELIVERED` não podem ser cancelados
- O total é calculado no domínio (`Order.total()`) — nunca no banco
- Cada `OrderItem` valida quantidade > 0 e preço > 0 na criação

---

## Decisões de design

**Por que os use cases não têm `@Service`?**
Anotar um use case com `@Service` cria uma dependência do Spring no núcleo da aplicação. O wiring é feito explicitamente em `BeanConfig`, mantendo os use cases como POJOs testáveis sem contexto Spring.

**Por que `OrderEntity` é separado de `Order`?**
`Order` é o objeto de domínio com comportamento e invariantes. `OrderEntity` é a representação relacional com anotações JPA. Misturá-los acoplaria o domínio ao banco de dados.

**Por que `SpringDataOrderRepository` é package-private?**
Apenas `OrderRepositoryImpl` pode acessar o repositório JPA. Nenhuma outra classe do projeto pode bypassar a interface de domínio e chamar o banco diretamente.

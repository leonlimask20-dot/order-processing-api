# Order Processing API

[![CI](https://github.com/leonlimask20-dot/order-processing-api/actions/workflows/ci.yml/badge.svg)](https://github.com/leonlimask20-dot/order-processing-api/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8-red?logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)

API REST de processamento de pedidos construída com Java 17 + Spring Boot 3.5, aplicando **Clean Architecture** (Ports & Adapters) para isolar o domínio de negócio de frameworks, banco de dados e detalhes de infraestrutura.

## Links rápidos

| | |
|---|---|
| Criar pedido | `POST http://localhost:8080/api/v1/orders` |
| Consultar pedido | `GET http://localhost:8080/api/v1/orders/{id}` |
| Cancelar pedido | `DELETE http://localhost:8080/api/v1/orders/{id}` |
| Rodar com Docker | [Ir para seção](#execução) |

## Principais competências demonstradas

- Clean Architecture com separação estrita entre domínio, aplicação e infraestrutura
- Ports & Adapters: `OrderRepository` definida no domínio, implementada na camada de persistência
- Dependency Inversion: use cases são POJOs puros — sem `@Service`, sem dependência de Spring
- Entidades de domínio com invariantes e comportamento encapsulado
- DTOs de entrada e saída separados das entidades de domínio e JPA
- Persistência com Spring Data JPA e PostgreSQL
- Tratamento centralizado de erros com `@RestControllerAdvice`
- Validação de entrada com Bean Validation
- Containerização com Docker e Docker Compose

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.13 |
| Spring Data JPA | 3.x |
| Hibernate | 6.6 |
| PostgreSQL | 16 |
| Lombok | — |
| Docker + Docker Compose | — |

## Fluxo de um pedido

```
1. POST /api/v1/orders        →  PlaceOrderRequest (DTO de entrada)
2. OrderController            →  mapeia DTO para List<OrderItem> do domínio
3. PlaceOrderUseCase          →  cria Order, aplica regras de negócio
4. OrderRepository (porta)    →  interface definida no domínio
5. OrderRepositoryImpl        →  implementa a porta usando JPA
6. OrderResponseMapper        →  converte Order (domínio) para OrderResponse (DTO de saída)
```

O domínio nunca conhece JPA, Spring ou HTTP. A direção das dependências sempre aponta para dentro.

## Arquitetura

```
src/main/java/com/lnl/orderprocessing/
├── domain/                    → Núcleo. Zero dependências externas.
│   ├── entity/                │   Order, OrderItem, Customer
│   ├── enums/                 │   OrderStatus
│   ├── event/                 │   OrderPlacedEvent
│   └── repository/            │   OrderRepository (interface — porta de saída)
│
├── application/               → Casos de uso. Orquestra o domínio.
│   └── usecase/               │   PlaceOrderUseCase, CancelOrderUseCase, TrackOrderUseCase
│                              │   POJOs puros — sem @Service, sem Spring
│
├── adapters/                  → Tradutores entre domínio e mundo externo
│   ├── controller/            │   OrderController, PlaceOrderRequest (entrada HTTP)
│   ├── presenter/             │   OrderResponseMapper, OrderResponse (saída HTTP)
│   └── persistence/           │   OrderEntity (JPA), OrderRepositoryImpl (implementa a porta)
│
└── infrastructure/            → Configuração e tratamento de erros
    ├── config/                │   BeanConfig — wiring dos use cases com Spring
    └── exception/             │   GlobalExceptionHandler
```

## Regras de negócio

**Ciclo de vida do pedido:**

```
PENDING → CONFIRMED → IN_PREPARATION → OUT_FOR_DELIVERY → DELIVERED
                                                              ↑
                                                    (não pode cancelar)
```

- Pedido nasce com status `PENDING`
- Só pedidos `PENDING` podem ser confirmados
- Pedidos `DELIVERED` não podem ser cancelados
- O total é calculado no domínio (`Order.total()`) — nunca no banco ou no controller
- Cada `OrderItem` valida quantidade > 0 e preço > 0 na construção

**Decisões de design:**

> **Por que os use cases não têm `@Service`?**
> Anotar um use case com `@Service` cria uma dependência do Spring no núcleo da aplicação. O wiring é feito explicitamente em `BeanConfig`, mantendo os use cases como POJOs testáveis sem contexto Spring.

> **Por que `OrderEntity` é separado de `Order`?**
> `Order` é o objeto de domínio com comportamento e invariantes. `OrderEntity` é a representação relacional com anotações JPA. Misturá-los acoplaria o domínio ao banco de dados.

> **Por que `SpringDataOrderRepository` é package-private?**
> Apenas `OrderRepositoryImpl` pode acessar o repositório JPA. Nenhuma outra classe pode bypassar a interface de domínio e chamar o banco diretamente.

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker Desktop

## Execução

```bash
# Sobe o banco PostgreSQL
docker-compose up -d

# Sobe a aplicação
mvn spring-boot:run
```

API disponível em `http://localhost:8080`.

> **Nota:** se você já tiver um PostgreSQL rodando na porta 5432, o Docker usa a porta 5433 neste projeto para evitar conflito.

O Hibernate cria as tabelas automaticamente na primeira execução (`ddl-auto=update`).

## Endpoints

### Pedidos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/orders` | Criar pedido |
| `GET` | `/api/v1/orders/{id}` | Consultar pedido |
| `DELETE` | `/api/v1/orders/{id}` | Cancelar pedido |

## Exemplos

### Criar pedido

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "cliente-001",
    "items": [
      {
        "productId": "prod-001",
        "productName": "X-Burguer",
        "quantity": 2,
        "unitPrice": 25.90
      }
    ]
  }'
```

```json
{
  "id": "f796cfd2-3413-4666-892e-f19c81a9c7c3",
  "customerId": "cliente-001",
  "status": "PENDING",
  "total": 51.80,
  "items": [
    {
      "productId": "prod-001",
      "productName": "X-Burguer",
      "quantity": 2,
      "unitPrice": 25.90,
      "subtotal": 51.80
    }
  ],
  "createdAt": "2026-04-11T18:03:04"
}
```

### Consultar pedido

```bash
curl http://localhost:8080/api/v1/orders/f796cfd2-3413-4666-892e-f19c81a9c7c3
```

### Cancelar pedido

```bash
curl -X DELETE http://localhost:8080/api/v1/orders/f796cfd2-3413-4666-892e-f19c81a9c7c3
```

## Considerações para produção

- Substituir `ddl-auto=update` por migrations com Flyway ou Liquibase
- Adicionar testes de integração com Testcontainers para paridade com o banco de produção
- Implementar paginação nos endpoints de listagem
- Adicionar eventos de domínio com publicação via RabbitMQ ou Kafka ao confirmar pedidos
- Configurar HTTPS com certificado TLS

## Autor

LNL &nbsp; GitHub: [@leonlimask20-dot](https://github.com/leonlimask20-dot) &nbsp; Email: leonlimask@gmail.com

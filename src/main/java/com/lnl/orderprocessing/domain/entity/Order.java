package com.lnl.orderprocessing.domain.entity;

import com.lnl.orderprocessing.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {

    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(String customerId, List<OrderItem> items) {
        if (customerId == null || customerId.isBlank())
            throw new IllegalArgumentException("customerId obrigatorio");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("pedido deve ter ao menos um item");

        this.id         = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.items      = new ArrayList<>(items);
        this.status     = OrderStatus.PENDING;
        this.createdAt  = LocalDateTime.now();
    }

    // Construtor usado pela camada de persistencia para reconstituir o objeto
    public Order(String id, String customerId, List<OrderItem> items, OrderStatus status, LocalDateTime createdAt) {
        this.id         = id;
        this.customerId = customerId;
        this.items      = new ArrayList<>(items);
        this.status     = status;
        this.createdAt  = createdAt;
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING)
            throw new IllegalStateException("apenas pedidos PENDING podem ser confirmados");
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED)
            throw new IllegalStateException("pedido ja entregue nao pode ser cancelado");
        this.status = OrderStatus.CANCELLED;
    }

    public BigDecimal total() {
        return items.stream()
            .map(OrderItem::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getId()                { return id; }
    public String getCustomerId()        { return customerId; }
    public OrderStatus getStatus()       { return status; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public List<OrderItem> getItems()    { return Collections.unmodifiableList(items); }
}

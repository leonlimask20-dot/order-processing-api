package com.lnl.orderprocessing.adapters.persistence;

import com.lnl.orderprocessing.domain.enums.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade JPA -- representa a tabela no banco de dados.
 * Propositalmente separada da entidade de dominio Order.
 * O dominio nao pode depender de anotacoes de framework.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String customerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItemEntity> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected OrderEntity() {}

    public OrderEntity(String id, String customerId, List<OrderItemEntity> items,
                       OrderStatus status, LocalDateTime createdAt) {
        this.id         = id;
        this.customerId = customerId;
        this.items      = items;
        this.status     = status;
        this.createdAt  = createdAt;
    }

    public String getId()                  { return id; }
    public String getCustomerId()          { return customerId; }
    public List<OrderItemEntity> getItems(){ return items; }
    public OrderStatus getStatus()         { return status; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public void setStatus(OrderStatus s)   { this.status = s; }
}

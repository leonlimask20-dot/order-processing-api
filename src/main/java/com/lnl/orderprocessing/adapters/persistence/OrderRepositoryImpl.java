package com.lnl.orderprocessing.adapters.persistence;

import com.lnl.orderprocessing.domain.entity.Order;
import com.lnl.orderprocessing.domain.entity.OrderItem;
import com.lnl.orderprocessing.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Implementa a porta de saida OrderRepository (definida no dominio).
 * Faz a traducao entre Order (dominio) e OrderEntity (JPA).
 * O dominio nunca sabe que este adaptador existe.
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final SpringDataOrderRepository jpa;

    public OrderRepositoryImpl(SpringDataOrderRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity saved  = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return jpa.findByCustomerId(customerId).stream()
            .map(this::toDomain)
            .toList();
    }

    private OrderEntity toEntity(Order order) {
        List<OrderItemEntity> itemEntities = order.getItems().stream()
            .map(i -> new OrderItemEntity(i.getProductId(), i.getProductName(),
                                          i.getQuantity(), i.getUnitPrice()))
            .toList();
        return new OrderEntity(order.getId(), order.getCustomerId(),
                               itemEntities, order.getStatus(), order.getCreatedAt());
    }

    private Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
            .map(i -> new OrderItem(i.getProductId(), i.getProductName(),
                                    i.getQuantity(), i.getUnitPrice()))
            .toList();
        return new Order(entity.getId(), entity.getCustomerId(),
                         items, entity.getStatus(), entity.getCreatedAt());
    }
}

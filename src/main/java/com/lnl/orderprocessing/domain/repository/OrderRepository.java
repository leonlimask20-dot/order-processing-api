package com.lnl.orderprocessing.domain.repository;

import com.lnl.orderprocessing.domain.entity.Order;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saida -- definida no dominio, implementada na camada de persistencia.
 * O dominio nunca conhece JPA, Hibernate ou qualquer detalhe de banco.
 */
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findByCustomerId(String customerId);
}

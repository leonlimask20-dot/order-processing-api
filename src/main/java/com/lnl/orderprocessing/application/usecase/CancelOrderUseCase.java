package com.lnl.orderprocessing.application.usecase;

import com.lnl.orderprocessing.domain.entity.Order;
import com.lnl.orderprocessing.domain.repository.OrderRepository;

public class CancelOrderUseCase {

    private final OrderRepository orderRepository;

    public CancelOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order execute(String orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("pedido nao encontrado: " + orderId));
        order.cancel();
        return orderRepository.save(order);
    }
}

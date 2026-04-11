package com.lnl.orderprocessing.application.usecase;

import com.lnl.orderprocessing.domain.entity.Order;
import com.lnl.orderprocessing.domain.repository.OrderRepository;

public class TrackOrderUseCase {

    private final OrderRepository orderRepository;

    public TrackOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order execute(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("pedido nao encontrado: " + orderId));
    }
}

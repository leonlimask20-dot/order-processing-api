package com.lnl.orderprocessing.application.usecase;

import com.lnl.orderprocessing.domain.entity.Order;
import com.lnl.orderprocessing.domain.entity.OrderItem;
import com.lnl.orderprocessing.domain.repository.OrderRepository;
import java.util.List;

public class PlaceOrderUseCase {

    private final OrderRepository orderRepository;

    public PlaceOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order execute(String customerId, List<OrderItem> items) {
        Order order = new Order(customerId, items);
        return orderRepository.save(order);
    }
}

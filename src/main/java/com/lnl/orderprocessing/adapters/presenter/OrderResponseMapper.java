package com.lnl.orderprocessing.adapters.presenter;

import com.lnl.orderprocessing.domain.entity.Order;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OrderResponseMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderResponse.ItemResponse> items = order.getItems().stream()
            .map(i -> new OrderResponse.ItemResponse(
                i.getProductId(),
                i.getProductName(),
                i.getQuantity(),
                i.getUnitPrice(),
                i.subtotal()
            ))
            .toList();

        return new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getStatus().name(),
            order.total(),
            items,
            order.getCreatedAt()
        );
    }
}

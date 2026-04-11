package com.lnl.orderprocessing.adapters.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

// DTO de entrada -- representa o corpo do POST /api/v1/orders
public record PlaceOrderRequest(
    @NotBlank String customerId,
    @NotEmpty List<ItemRequest> items
) {
    public record ItemRequest(
        @NotBlank String productId,
        @NotBlank String productName,
        int quantity,
        BigDecimal unitPrice
    ) {}
}

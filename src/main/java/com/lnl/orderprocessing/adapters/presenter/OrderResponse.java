package com.lnl.orderprocessing.adapters.presenter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// DTO de saida -- o que o cliente da API recebe, nunca o objeto de dominio diretamente
public record OrderResponse(
    String id,
    String customerId,
    String status,
    BigDecimal total,
    List<ItemResponse> items,
    LocalDateTime createdAt
) {
    public record ItemResponse(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {}
}

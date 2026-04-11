package com.lnl.orderprocessing.domain.event;

import java.time.LocalDateTime;

public class OrderPlacedEvent {

    private final String orderId;
    private final String customerId;
    private final LocalDateTime occurredAt;

    public OrderPlacedEvent(String orderId, String customerId) {
        this.orderId    = orderId;
        this.customerId = customerId;
        this.occurredAt = LocalDateTime.now();
    }

    public String getOrderId()           { return orderId; }
    public String getCustomerId()        { return customerId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}

package com.lnl.orderprocessing.adapters.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Embeddable
public class OrderItemEntity {

    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    protected OrderItemEntity() {}

    public OrderItemEntity(String productId, String productName, int quantity, BigDecimal unitPrice) {
        this.productId   = productId;
        this.productName = productName;
        this.quantity    = quantity;
        this.unitPrice   = unitPrice;
    }

    public String getProductId()    { return productId; }
    public String getProductName()  { return productName; }
    public int getQuantity()        { return quantity; }
    public BigDecimal getUnitPrice(){ return unitPrice; }
}

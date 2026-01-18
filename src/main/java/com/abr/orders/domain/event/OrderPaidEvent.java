package com.abr.orders.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderPaidEvent extends DomainEvent {

    private final UUID orderId;
    private final BigDecimal totalAmount;

    public OrderPaidEvent(UUID orderId, BigDecimal totalAmount) {
        super();
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}

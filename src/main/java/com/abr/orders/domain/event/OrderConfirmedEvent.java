package com.abr.orders.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderConfirmedEvent extends DomainEvent {

    private final UUID orderId;
    private final BigDecimal totalAmount;

    public OrderConfirmedEvent(UUID orderId, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public UUID getAggregateId() {
        return orderId;
    }

    @Override
    public String getAggregateType() {
        return "Order";
    }

}

package com.abr.orders.domain.event;

import java.util.UUID;

public class OrderCanceledEvent extends DomainEvent {

    private final UUID orderId;

    public OrderCanceledEvent(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    @Override
    public UUID getAggregateId() {
        return orderId;
    }

    @Override
    public String getAggregateType() {
        return "OrderCanceled";
    }
}

package com.abr.orders.domain.event;

import java.util.UUID;

public class OrderShippedEvent extends DomainEvent {

    private final UUID orderId;

    public OrderShippedEvent(UUID orderId) {
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
        return "OrderShipped";
    }
}

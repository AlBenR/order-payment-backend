package com.abr.orders.domain.event;

import java.util.UUID;

public class OrderConfirmedEvent extends DomainEvent {

    private final UUID orderId;

    public OrderConfirmedEvent(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}

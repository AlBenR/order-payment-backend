package com.abr.payment.domain.event;

import com.abr.payment.domain.model.OrderId;

import java.util.UUID;

public final class PaymentAuthorized extends PaymentEvent {

    private final OrderId orderId;

    public PaymentAuthorized(OrderId orderId) {
        this.orderId = orderId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    @Override
    public UUID getAggregateId() {
        return orderId.value();
    }

    @Override
    public String getAggregateType() {
        return "Payment";
    }

    @Override
    public String getEventType() {
        return "PaymentAuthorized";
    }
}


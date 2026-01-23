package com.abr.payment.domain.event;

import com.abr.payment.domain.model.OrderId;

import java.util.UUID;

public final class PaymentFailed extends PaymentEvent {

    private final OrderId orderId;
    private final String reason;

    public PaymentFailed(OrderId orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
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
        return "PaymentFailed";
    }
}

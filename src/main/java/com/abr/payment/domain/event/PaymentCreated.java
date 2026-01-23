package com.abr.payment.domain.event;

import com.abr.payment.domain.model.Money;
import com.abr.payment.domain.model.OrderId;

import java.util.UUID;

public final class PaymentCreated extends PaymentEvent {

    private final OrderId orderId;
    private final Money amount;

    public PaymentCreated(OrderId orderId, Money amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Money getAmount() {
        return amount;
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
        return "PaymentCreated";
    }
}

package com.abr.payment.domain.model;

import com.abr.payment.domain.event.PaymentAuthorized;
import com.abr.payment.domain.event.PaymentCreated;
import com.abr.payment.domain.event.PaymentEvent;
import com.abr.payment.domain.event.PaymentFailed;

import java.util.ArrayList;
import java.util.List;

public class Payment {

    private final OrderId orderId;
    private final Money amount;
    private PaymentStatus status;

    private final List<PaymentEvent> domainEvents = new ArrayList<>();

    private Payment(OrderId orderId, Money amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.CREATED;
        this.domainEvents.add(new PaymentCreated(orderId, amount));
    }

    // For mapper
    public static Payment restore(
            OrderId orderId,
            Money amount,
            PaymentStatus status
    ) {
        Payment payment = new Payment(orderId, amount);
        payment.status = status;
        payment.domainEvents.clear();
        return payment;
    }

    public static Payment create(OrderId orderId, Money amount) {
        return new Payment(orderId, amount);
    }

    public void authorize() {
        if (status != PaymentStatus.CREATED) {
            throw new IllegalStateException(
                    "Payment cannot be authorized in status " + status
            );
        }
        this.status = PaymentStatus.AUTHORIZED;
        this.domainEvents.add(new PaymentAuthorized(orderId));
    }

    public void fail(String reason) {
        if (status != PaymentStatus.CREATED) {
            throw new IllegalStateException(
                    "Payment cannot fail in status " + status
            );
        }
        this.status = PaymentStatus.FAILED;
        this.domainEvents.add(new PaymentFailed(orderId, reason));
    }

    public List<PaymentEvent> pullDomainEvents() {
        List<PaymentEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public List<PaymentEvent> getDomainEvents() {
        return domainEvents;
    }
}

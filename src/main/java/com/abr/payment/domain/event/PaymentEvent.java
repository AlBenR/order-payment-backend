package com.abr.payment.domain.event;

import com.abr.orders.domain.event.DomainEvent;

public abstract sealed class PaymentEvent extends DomainEvent
        permits PaymentCreated, PaymentAuthorized, PaymentFailed {
}

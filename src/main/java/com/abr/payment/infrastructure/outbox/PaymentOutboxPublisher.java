package com.abr.payment.infrastructure.outbox;

import com.abr.payment.domain.ports.out.DomainEventPublisher;
import com.abr.shared.outbox.OutboxPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentOutboxPublisher implements DomainEventPublisher {

    private final OutboxPublisher outboxPublisher;

    public PaymentOutboxPublisher(OutboxPublisher outboxPublisher) {
        this.outboxPublisher = outboxPublisher;
    }

    @Override
    public void publish(Object event) {
        outboxPublisher.publish(event);
    }
}


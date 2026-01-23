package com.abr.orders.infrastructure.outbox.adapter;

import com.abr.orders.domain.event.DomainEvent;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.shared.outbox.OutboxPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private final OutboxPublisher outboxPublisher;

    public OutboxDomainEventPublisher(OutboxPublisher outboxPublisher) {
        this.outboxPublisher = outboxPublisher;
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(outboxPublisher::publish);
    }
}
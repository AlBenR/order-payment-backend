package com.abr.orders.infrastructure.outbox.adapter;

import com.abr.orders.domain.event.DomainEvent;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import com.abr.orders.infrastructure.outbox.repository.JpaOutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private final JpaOutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxDomainEventPublisher(
            JpaOutboxEventRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(event -> {
            OutboxEventEntity entity =
                    OutboxEventEntity.from(event, objectMapper);
            repository.save(entity);
        });
    }
}
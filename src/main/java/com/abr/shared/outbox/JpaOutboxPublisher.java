package com.abr.shared.outbox;

import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import com.abr.orders.infrastructure.outbox.repository.JpaOutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JpaOutboxPublisher implements OutboxPublisher {

    private final JpaOutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public JpaOutboxPublisher(
            JpaOutboxEventRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Object event) {
        OutboxEventEntity entity =
                OutboxEventEntity.from(event, objectMapper);
        repository.save(entity);
    }
}


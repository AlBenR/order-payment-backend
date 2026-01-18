package com.abr.orders.infrastructure.outbox.entity;

import com.abr.orders.domain.event.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String aggregateType;

    private UUID aggregateId;

    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    private Instant occurredOn;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts = 0;

    private Instant lastAttemptAt;

    protected OutboxEventEntity() {}

    protected OutboxEventEntity(
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String payload,
            Instant occurredOn,
            OutboxStatus status
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredOn = occurredOn;
        this.status = status;
    }

    public static OutboxEventEntity from(DomainEvent event, ObjectMapper objectMapper
    ) {
        try {
            return new OutboxEventEntity(
                    "Order",
                    event.getEventId(),
                    event.getClass().getSimpleName(),
                    objectMapper.writeValueAsString(event),
                    event.getOccurredAt(),
                    OutboxStatus.PENDING
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize domain event", e);
        }
    }

    public void markAsSent() {
        this.status = OutboxStatus.SENT;
    }

    public void markAsFailed() {
        this.status = OutboxStatus.FAILED;
    }

    public void registerFailure() {
        this.attempts++;
        this.lastAttemptAt = Instant.now();
    }

    public boolean hasReachedMaxAttempts() {
        return this.attempts >= 5;
    }

    public boolean canRetry(Instant now) {
        if (this.lastAttemptAt == null) {
            return true;
        }

        Duration backoff = switch (this.attempts) {
            case 0, 1 -> Duration.ofSeconds(1);
            case 2 -> Duration.ofSeconds(5);
            case 3 -> Duration.ofSeconds(15);
            default -> Duration.ofSeconds(30);
        };

        return this.lastAttemptAt.plus(backoff).isBefore(now);
    }
}

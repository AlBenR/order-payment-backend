package com.abr.orders.infrastructure.outbox.entity;

import com.abr.orders.domain.event.DomainEvent;
import com.abr.shared.event.GettersOutboxEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Setter
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

    public static OutboxEventEntity from(
            Object event,
            ObjectMapper objectMapper
    ) {
        try {
            if (!(event instanceof com.abr.shared.event.GettersOutboxEvent gettersOutboxEvent)) {
                throw new IllegalArgumentException(
                        "Event does not implement OutboxEvent: " + event.getClass()
                );
            }
            OutboxEventEntity entity = new OutboxEventEntity();

            entity.setAggregateType(gettersOutboxEvent.getAggregateType());
            entity.setAggregateId(gettersOutboxEvent.getAggregateId());
            entity.setEventType(event.getClass().getSimpleName());
            entity.setPayload(objectMapper.writeValueAsString(event));
            entity.setOccurredOn(Instant.now());
            entity.setStatus(OutboxStatus.PENDING);

            return entity;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize outbox event", e);
        }
    }

    public boolean isInternal() {
        return aggregateType.equals("Order")
                || aggregateType.equals("Payment");
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

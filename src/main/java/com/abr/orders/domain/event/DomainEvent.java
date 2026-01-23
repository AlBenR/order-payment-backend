package com.abr.orders.domain.event;

import com.abr.shared.event.GettersOutboxEvent;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent implements GettersOutboxEvent {

    private final UUID eventId;
    private final Instant occurredAt;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getEventType() {
        return this.getClass().getSimpleName();
    }

}

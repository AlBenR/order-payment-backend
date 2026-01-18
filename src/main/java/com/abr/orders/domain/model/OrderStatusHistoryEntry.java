package com.abr.orders.domain.model;

import java.time.Instant;
import java.util.Objects;

public class OrderStatusHistoryEntry {

    private final OrderStatus status;
    private final OrderStatus previousStatus;
    private final Instant changedAt;

    public OrderStatusHistoryEntry(
            OrderStatus status,
            OrderStatus previousStatus,
            Instant changedAt
    ) {
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.previousStatus = previousStatus; // Can be null (when an Order is created)
        this.changedAt = Objects.requireNonNull(changedAt, "changedAt must not be null");
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}

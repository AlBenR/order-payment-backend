package com.abr.orders.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "processed_payments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "idempotency_key")
        }
)
public class ProcessedPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private Instant processedAt;

    protected ProcessedPaymentEntity() {
        // Empty constructor
    }

    public ProcessedPaymentEntity(UUID orderId, String idempotencyKey) {
        this.orderId = orderId;
        this.idempotencyKey = idempotencyKey;
        this.processedAt = Instant.now();
    }
}


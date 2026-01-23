package com.abr.payment.infrastructure.persistence.entity;

import com.abr.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    protected PaymentEntity() {
    }

    public PaymentEntity(
            UUID orderId,
            BigDecimal amount,
            PaymentStatus status
    ) {

        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }
}


package com.abr.orders.infrastructure.persistence.entity;

import com.abr.orders.domain.model.OrderStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderStatusHistoryEmbeddable {

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private OrderStatus previousStatus;

    private Instant changedAt;
}

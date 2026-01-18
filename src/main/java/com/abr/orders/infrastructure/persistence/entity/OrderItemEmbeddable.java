package com.abr.orders.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class OrderItemEmbeddable {

    private UUID productId;
    private int quantity;
    private BigDecimal price;
}

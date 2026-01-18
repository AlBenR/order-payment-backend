package com.abr.orders.infrastructure.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    private UUID productId;
    private int quantity;
    private BigDecimal price;

}

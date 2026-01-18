package com.abr.orders.infrastructure.rest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemResponse {

    private UUID productId;
    private int quantity;
    private BigDecimal price;

}

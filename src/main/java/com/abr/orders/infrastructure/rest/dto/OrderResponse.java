package com.abr.orders.infrastructure.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID id;
    private UUID customerId;
    private String status;
    private Instant createdAt;
    private List<OrderItemResponse> items;

}
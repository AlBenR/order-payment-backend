package com.abr.orders.infrastructure.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private UUID customerId;
    private List<OrderItemRequest> items;

}

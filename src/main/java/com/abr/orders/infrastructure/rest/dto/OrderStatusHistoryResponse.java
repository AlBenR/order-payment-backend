package com.abr.orders.infrastructure.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryResponse {

    private String status;
    private String previousStatus;
    private Instant changedAt;

}

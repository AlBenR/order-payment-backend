package com.abr.orders.domain.ports.in;

import com.abr.orders.domain.model.IdempotencyKey;

import java.util.UUID;

public interface PayOrderUseCase {

    void pay(UUID orderId, IdempotencyKey idempotencyKey);
}

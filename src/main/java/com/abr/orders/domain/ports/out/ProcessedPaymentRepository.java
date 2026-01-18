package com.abr.orders.domain.ports.out;

import com.abr.orders.domain.model.IdempotencyKey;

import java.util.UUID;

public interface ProcessedPaymentRepository {

    boolean existsByKey(IdempotencyKey key);

    void save(UUID orderId, IdempotencyKey key);
}

package com.abr.orders.domain.model;

import com.abr.orders.domain.exception.BusinessRuleViolationException;

public record IdempotencyKey(String value) {

    public IdempotencyKey {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolationException(
                    "Idempotency key cannot be null or blank"
            );
        }
    }
}

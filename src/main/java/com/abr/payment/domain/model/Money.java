package com.abr.payment.domain.model;

import java.math.BigDecimal;

public class Money {

    private final BigDecimal amount;

    protected Money() {
        this.amount = BigDecimal.ZERO;
    }

    public Money(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

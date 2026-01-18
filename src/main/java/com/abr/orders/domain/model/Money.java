package com.abr.orders.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Money {

    private final BigDecimal amount;

    public Money(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        this.amount = amount;
    }

    public Money add(Money other){
        return new Money(this.amount.add(other.amount));
    }

    public BigDecimal getAmount(){
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}

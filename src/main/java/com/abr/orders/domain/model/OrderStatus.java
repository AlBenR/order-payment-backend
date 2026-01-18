package com.abr.orders.domain.model;

import com.abr.orders.domain.exception.BusinessRuleViolationException;

public enum OrderStatus {
    CREATED{
        @Override
        public void validateConfirm(){}
        @Override
        public void validateCancel(){}
    },
    CONFIRMED{
        @Override
        public void validatePay(){}
        @Override
        public void validateCancel(){}
    },
    PAID{
        @Override
        public void validateCancel(){}
        @Override
        public void validateShip(){}
    },
    SHIPPED,
    CANCELLED;

    public void validateConfirm() {
        throw new BusinessRuleViolationException(
                "Order cannot be confirmed from state " + this
        );
    }

    public void validatePay() {
        throw new BusinessRuleViolationException(
                "Only CONFIRMED orders can be paid"
        );
    }

    public void validateCancel() {
        throw new BusinessRuleViolationException(
                "Order cannot be cancelled when status is " + this
        );
    }

    public void validateShip() {
        throw new BusinessRuleViolationException(
                "Only PAID orders can be shipped"
        );
    }
}

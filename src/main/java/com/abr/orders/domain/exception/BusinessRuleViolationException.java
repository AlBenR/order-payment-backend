package com.abr.orders.domain.exception;

public class BusinessRuleViolationException extends RuntimeException{
    public BusinessRuleViolationException(String message){
        super(message);
    }
}

package com.abr.orders.application.exception;

public class ConcurrentOrderModificationException extends RuntimeException {

    public ConcurrentOrderModificationException() {
        super("Order was modified concurrently. Please retry.");
    }
}

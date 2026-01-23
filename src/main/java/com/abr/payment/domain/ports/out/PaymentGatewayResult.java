package com.abr.payment.domain.ports.out;

public sealed interface PaymentGatewayResult
        permits PaymentGatewayResult.Authorized,
        PaymentGatewayResult.Failed {

    record Authorized() implements PaymentGatewayResult {}

    record Failed(String reason) implements PaymentGatewayResult {}
}

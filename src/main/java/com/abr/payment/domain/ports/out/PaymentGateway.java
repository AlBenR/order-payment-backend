package com.abr.payment.domain.ports.out;

import com.abr.payment.domain.model.Payment;

public interface PaymentGateway {

    PaymentGatewayResult authorize(Payment payment);
}

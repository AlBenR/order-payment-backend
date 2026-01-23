package com.abr.payment.infrastructure.gateway;

import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.ports.out.PaymentGateway;
import com.abr.payment.domain.ports.out.PaymentGatewayResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FakePaymentGatewayAdapter implements PaymentGateway {

    @Override
    public PaymentGatewayResult authorize(Payment payment) {

        if (payment.getAmount().getAmount().compareTo(BigDecimal.valueOf(1000)) < 0) {
            return new PaymentGatewayResult.Authorized();
        }
        return new PaymentGatewayResult.Failed("Limit exceeded");
    }
}

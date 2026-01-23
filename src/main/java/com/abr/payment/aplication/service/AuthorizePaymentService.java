package com.abr.payment.aplication.service;

import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.model.PaymentId;
import com.abr.payment.domain.ports.in.AuthorizePaymentCommand;
import com.abr.payment.domain.ports.in.AuthorizePaymentUseCase;
import com.abr.payment.domain.ports.out.DomainEventPublisher;
import com.abr.payment.domain.ports.out.PaymentGateway;
import com.abr.payment.domain.ports.out.PaymentGatewayResult;
import com.abr.payment.domain.ports.out.PaymentRepository;

public class AuthorizePaymentService implements AuthorizePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final DomainEventPublisher eventPublisher;

    public AuthorizePaymentService(
            PaymentRepository paymentRepository,
            PaymentGateway paymentGateway,
            DomainEventPublisher eventPublisher
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void authorize(AuthorizePaymentCommand command) {

        Payment payment = paymentRepository.findById(
                new OrderId(command.orderId())
        ).orElseThrow(() ->
                new IllegalArgumentException("Payment not found")
        );

        PaymentGatewayResult result = paymentGateway.authorize(payment);

        if (result instanceof PaymentGatewayResult.Authorized) {
            payment.authorize();
        } else if (result instanceof PaymentGatewayResult.Failed failed) {
            payment.fail(failed.reason());
        }

        paymentRepository.save(payment);
        eventPublisher.publish(payment.pullDomainEvents());
    }
}


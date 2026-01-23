package com.abr.payment.aplication.service;

import com.abr.payment.domain.model.Money;
import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.model.PaymentId;
import com.abr.payment.domain.ports.in.CreatePaymentCommand;
import com.abr.payment.domain.ports.in.CreatePaymentUseCase;
import com.abr.payment.domain.ports.out.DomainEventPublisher;
import com.abr.payment.domain.ports.out.PaymentRepository;

public class CreatePaymentService implements CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final DomainEventPublisher eventPublisher;

    public CreatePaymentService(
            PaymentRepository paymentRepository,
            DomainEventPublisher eventPublisher
    ) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public OrderId create(CreatePaymentCommand command) {

        Payment payment = Payment.create(
                new OrderId(command.orderId()),
                new Money(command.amount())
        );

        paymentRepository.save(payment);
        eventPublisher.publish(payment.pullDomainEvents());

        return payment.getOrderId();
    }
}

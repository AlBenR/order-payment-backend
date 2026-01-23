package com.abr.payment.aplication.service;

import com.abr.payment.domain.model.Money;
import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.ports.out.DomainEventPublisher;
import com.abr.payment.domain.ports.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreatePaymentFromOrderService {

    private final PaymentRepository paymentRepository;
    private final DomainEventPublisher eventPublisher;

    public CreatePaymentFromOrderService(
            PaymentRepository paymentRepository,
            DomainEventPublisher eventPublisher
    ) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void createFromOrder(UUID orderId, Money amount) {

        OrderId id = new OrderId(orderId);


        if (paymentRepository.existsByOrderId(id)) {
            return;
        }

        Payment payment = Payment.create(id, amount);

        paymentRepository.save(payment);
        payment.pullDomainEvents()
                .forEach(eventPublisher::publish);
    }
}


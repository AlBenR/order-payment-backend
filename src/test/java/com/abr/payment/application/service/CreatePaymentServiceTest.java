package com.abr.payment.application.service;

import com.abr.payment.aplication.service.CreatePaymentService;
import com.abr.payment.domain.model.Money;
import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.model.PaymentId;
import com.abr.payment.domain.ports.in.CreatePaymentCommand;
import com.abr.payment.domain.ports.out.DomainEventPublisher;
import com.abr.payment.domain.ports.out.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    CreatePaymentService service;

    @Test
    void should_create_payment_and_publish_events() {
        // given
        UUID orderId = UUID.randomUUID();

        CreatePaymentCommand command = new CreatePaymentCommand(
                orderId,
                BigDecimal.valueOf(100)
        );

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderId newPayment = service.create(command);

        // then
        assertThat(newPayment).isNotNull();

        verify(paymentRepository).save(any(Payment.class));
        verify(eventPublisher).publish(anyList());
    }
}

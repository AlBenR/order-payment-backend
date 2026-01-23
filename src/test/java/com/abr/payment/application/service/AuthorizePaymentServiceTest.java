package com.abr.payment.application.service;

import com.abr.payment.aplication.service.AuthorizePaymentService;
import com.abr.payment.domain.model.Money;
import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.model.PaymentStatus;
import com.abr.payment.domain.ports.in.AuthorizePaymentCommand;
import com.abr.payment.domain.ports.out.DomainEventPublisher;
import com.abr.payment.domain.ports.out.PaymentGateway;
import com.abr.payment.domain.ports.out.PaymentGatewayResult;
import com.abr.payment.domain.ports.out.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizePaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PaymentGateway paymentGateway;

    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    AuthorizePaymentService service;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );
    }

    @Test
    void should_authorize_payment_when_gateway_approves() {
        when(paymentRepository.findById(payment.getOrderId()))
                .thenReturn(Optional.of(payment));

        when(paymentGateway.authorize(payment))
                .thenReturn(new PaymentGatewayResult.Authorized());

        service.authorize(
                new AuthorizePaymentCommand(payment.getOrderId().value())
        );

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.AUTHORIZED);

        verify(paymentRepository).save(payment);
        verify(eventPublisher).publish(anyList());
    }

    @Test
    void should_fail_payment_when_gateway_rejects() {
        when(paymentRepository.findById(payment.getOrderId()))
                .thenReturn(Optional.of(payment));

        when(paymentGateway.authorize(payment))
                .thenReturn(
                        new PaymentGatewayResult.Failed("Insufficient funds")
                );

        service.authorize(
                new AuthorizePaymentCommand(payment.getOrderId().value())
        );

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.FAILED);

        verify(paymentRepository).save(payment);
        verify(eventPublisher).publish(anyList());
    }

    @Test
    void should_throw_exception_when_payment_not_found() {
        when(paymentRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.authorize(
                        new AuthorizePaymentCommand(UUID.randomUUID())
                )
        ).isInstanceOf(IllegalArgumentException.class);

        verify(paymentRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

}

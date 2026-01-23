package com.abr.payment.domain;

import com.abr.payment.domain.event.PaymentAuthorized;
import com.abr.payment.domain.event.PaymentCreated;
import com.abr.payment.domain.event.PaymentEvent;
import com.abr.payment.domain.event.PaymentFailed;
import com.abr.payment.domain.model.Money;
import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class paymentTest {

    @Test
    void should_create_payment_in_created_status() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CREATED);
    }

    @Test
    void should_authorize_payment() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );

        payment.authorize();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void should_not_authorize_twice() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );
        payment.authorize();

        assertThatThrownBy(payment::authorize)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_fail_payment_in_CREATED_Status() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );

        payment.fail("Insufficient funds");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void should_not_fail_authorized_payment() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );
        payment.authorize();

        assertThatThrownBy(() -> payment.fail("error"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_not_authorize_failed_payment() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );
        payment.fail("error");

        assertThatThrownBy(payment::authorize)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_emit_payment_created_event() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );

        List<PaymentEvent> events = payment.pullDomainEvents();

        assertThat(events)
                .anyMatch(event -> event instanceof PaymentCreated);
    }

    @Test
    void should_emit_payment_authorized_event() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );

        payment.authorize();
        List<PaymentEvent> events = payment.pullDomainEvents();

        assertThat(events)
                .anyMatch(event -> event instanceof PaymentAuthorized);
    }

    @Test
    void should_emit_payment_failed_event() {
        Payment payment = Payment.create(
                new OrderId(UUID.randomUUID()),
                new Money(BigDecimal.valueOf(100))
        );

        payment.fail("error");
        List<PaymentEvent> events = payment.pullDomainEvents();

        assertThat(events)
                .anyMatch(event -> event instanceof PaymentFailed);
    }

    @Test
    void should_not_allow_negative_amount() {
        assertThatThrownBy(() ->
                new Money(BigDecimal.valueOf(-10))
        ).isInstanceOf(IllegalArgumentException.class);
    }

}

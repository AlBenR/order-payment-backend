package com.abr.orders.application.service;

import com.abr.orders.application.exception.ConcurrentOrderModificationException;
import com.abr.orders.domain.event.OrderPaidEvent;
import com.abr.orders.domain.exception.BusinessRuleViolationException;
import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.*;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.orders.domain.ports.out.OrderRepository;

import com.abr.orders.domain.ports.out.ProcessedPaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PayOrderServiceTest {

    private OrderRepository orderRepository;
    private DomainEventPublisher eventPublisher;
    private ProcessedPaymentRepository processedPaymentRepository;
    private PayOrderService service;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        eventPublisher = mock(DomainEventPublisher.class);
        processedPaymentRepository = mock(ProcessedPaymentRepository.class);
        service = new PayOrderService(orderRepository, eventPublisher, processedPaymentRepository);
    }

    @Test
    void shouldPayConfirmedOrder() {

        UUID orderId = UUID.randomUUID();
        IdempotencyKey key = new IdempotencyKey("key-1");
        Order order = Order.create(
                UUID.randomUUID(),
                List.of(
                        new OrderItem(
                                UUID.randomUUID(),
                                2,
                                new Money(new BigDecimal("10.00"))
                        )
                )
        );

        order.confirm(); // current state: confirmed

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        service.pay(orderId, key);

        verify(orderRepository).save(order);
    }

    @Test
    void shouldFailWhenOrderDoesNotExist() {

        UUID orderId = UUID.randomUUID();
        IdempotencyKey key = new IdempotencyKey("key-2");

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> service.pay(orderId, key)
        );
    }

    @Test
    void shouldNotPersistOrderWhenDomainRejectsPayment() {

        UUID orderId = UUID.randomUUID();
        IdempotencyKey key = new IdempotencyKey("key-3");

        Order order = Order.create(
                UUID.randomUUID(),
                List.of(
                        new OrderItem(
                                UUID.randomUUID(),
                                1,
                                new Money(new BigDecimal("10.00"))
                        )
                )
        );
        // Estado inicial = CREATED (non-confirmed order)

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                BusinessRuleViolationException.class,
                () -> service.pay(orderId, key)
        );

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldPublishDomainEventWhenOrderIsPaid() {

        UUID orderId = UUID.randomUUID();
        IdempotencyKey key = new IdempotencyKey("key-4");

        Order order = Order.create(
                UUID.randomUUID(),
                List.of(
                        new OrderItem(
                                UUID.randomUUID(),
                                1,
                                new Money(new BigDecimal("10.00"))
                        )
                )
        );

        order.confirm();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        service.pay(orderId, key);

        verify(orderRepository).save(order);
        verify(eventPublisher).publishAll(anyList());
    }

    @Test
    void shouldIgnoreDuplicatePayment() {

        UUID orderId = UUID.randomUUID();
        IdempotencyKey key = new IdempotencyKey("abc-123");

        when(processedPaymentRepository.existsByKey(key))
                .thenReturn(true);

        service.pay(orderId, key);

        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publishAll(any());
    }

    @Test
    void shouldFailWhenOrderIsModifiedConcurrently() {

        UUID orderId = UUID.randomUUID();
        IdempotencyKey key = new IdempotencyKey("key-123");

        when(processedPaymentRepository.existsByKey(key))
                .thenReturn(false);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(mock(Order.class)));

        doThrow(ObjectOptimisticLockingFailureException.class)
                .when(orderRepository).save(any());

        assertThrows(
                ConcurrentOrderModificationException.class,
                () -> service.pay(orderId, key)
        );

        verify(eventPublisher, never()).publishAll(any());
    }

}
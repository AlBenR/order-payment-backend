package com.abr.orders.application.service;

import com.abr.orders.domain.exception.BusinessRuleViolationException;
import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.Money;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.orders.domain.ports.out.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ConfirmOrderServiceTest {

    private OrderRepository orderRepository;
    private ConfirmOrderService service;
    private DomainEventPublisher eventPublisher;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        eventPublisher = mock(DomainEventPublisher.class);
        service = new ConfirmOrderService(orderRepository, eventPublisher);
    }

    @Test
    void shouldConfirmCreatedOrder() {

        UUID orderId = UUID.randomUUID();
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

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        service.confirm(orderId);

        verify(orderRepository).save(order);
    }

    @Test
    void shouldFailWhenOrderDoesNotExist() {

        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> service.confirm(orderId)
        );
    }

    @Test
    void shouldNotPersistWhenDomainRejectsConfirming() {

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

        order.cancel(Clock.systemUTC());

        UUID orderId = order.getId();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                BusinessRuleViolationException.class,
                () -> service.confirm(orderId)
        );

        verify(orderRepository, never()).save(any());
    }

}

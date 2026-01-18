package com.abr.orders.application.service;

import com.abr.orders.domain.exception.BusinessRuleViolationException;
import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.Money;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;
import com.abr.orders.domain.model.OrderStatus;
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

public class CancelOrderServiceTest {

    private OrderRepository orderRepository;
    private CancelOrderService service;
    private DomainEventPublisher eventPublisher;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        eventPublisher = mock(DomainEventPublisher.class);
        Clock fixedClock = Clock.fixed(
                Instant.parse("2024-01-01T10:00:00Z"),
                ZoneOffset.UTC
        );
        service = new CancelOrderService(orderRepository, eventPublisher, fixedClock);
    }

    @Test
    void shouldCancelPaidOrder() {

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

        order.confirm(); // Order is confirmed
        order.markAsPaid(); // Order is paid

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        service.cancel(orderId);

        verify(orderRepository).save(order);
    }

    @Test
    void shouldFailWhenOrderDoesNotExist() {

        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> service.cancel(orderId)
        );
    }

    @Test
    void shouldNotPersistOrderWhenDomainRejectsCancellation() {

        UUID orderId = UUID.randomUUID();

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
        // Current status: created
        // Current status: confirmed
        order.confirm();
        // Current status: paid
        order.markAsPaid();
        // Current status: shipped
        order.ship();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                BusinessRuleViolationException.class,
                () -> service.cancel(orderId)
        );

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldCancelPaidOrderWithinAllowedTime() {

        Clock clock = Clock.fixed(
                Instant.parse("2024-01-01T10:05:00Z"),
                ZoneOffset.UTC
        );

        Order order = Order.create(
                UUID.randomUUID(),
                List.of(                        new OrderItem(
                        UUID.randomUUID(),
                        1,
                        new Money(new BigDecimal("10.00"))
                ))
        );

        order.confirm();
        order.markAsPaid();

        when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        CancelOrderService service =
                new CancelOrderService(orderRepository, eventPublisher, clock);

        service.cancel(order.getId());

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

}

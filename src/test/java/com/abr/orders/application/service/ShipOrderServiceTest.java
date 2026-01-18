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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ShipOrderServiceTest {

    private OrderRepository orderRepository;
    private ShipOrderService service;
    private DomainEventPublisher eventPublisher;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        eventPublisher = mock(DomainEventPublisher.class);
        service = new ShipOrderService(orderRepository, eventPublisher);
    }

    @Test
    void shouldShipPaidOrder() {

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

        service.ship(orderId);

        verify(orderRepository).save(order);
    }

    @Test
    void shouldFailWhenOrderDoesNotExist() {

        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> service.ship(orderId)
        );
    }

    @Test
    void shouldNotPersistOrderWhenDomainRejectsShipping() {

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

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                BusinessRuleViolationException.class,
                () -> service.ship(orderId)
        );

        verify(orderRepository, never()).save(any());
    }
}

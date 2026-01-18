package com.abr.orders.domain.model;

import com.abr.orders.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderInCreatedState() {
        Order order = Order.create(
                UUID.randomUUID(),
                List.of(sampleItem())
        );

        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertFalse(order.getItems().isEmpty());
    }

    @Test
    void shouldConfirmOrder() {
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }


    @Test
    void shouldAddHistoryEntryWhenOrderIsConfirmed() {
        Order order = Order.create(UUID.randomUUID(),List.of(sampleItem()));

        order.confirm();

        assertEquals(2, order.getStatusHistory().size());
        assertEquals(OrderStatus.CREATED, order.getStatusHistory().get(0).getStatus());
        assertEquals(OrderStatus.CONFIRMED, order.getStatusHistory().get(1).getStatus());
    }


    @Test
    void shouldPayConfirmedOrder() {
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();
        order.markAsPaid();

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void CannotPayOrderIfNotConfirmed() {
        Order order = Order.create(
                UUID.randomUUID(),
                List.of(sampleItem()));

        assertThrows(
                BusinessRuleViolationException.class,
                order::markAsPaid
        );
    }

    @Test
    void shouldAddHistoryEntryWhenOrderIsPaid() {
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();
        order.markAsPaid();

        assertEquals(3, order.getStatusHistory().size());
        assertEquals(OrderStatus.CREATED, order.getStatusHistory().get(0).getStatus());
        assertEquals(OrderStatus.CONFIRMED, order.getStatusHistory().get(1).getStatus());
        assertEquals(OrderStatus.PAID, order.getStatusHistory().get(2).getStatus());
    }

    @Test
    void shouldCancelOrder() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-01-01T10:00:00Z"),
                ZoneOffset.UTC
        );
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();
        order.cancel(clock);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void CannotCancelShippedOrder(){

        Clock clock = Clock.fixed(
                Instant.parse("2026-01-01T10:00:00Z"),
                ZoneOffset.UTC
        );
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();
        order.markAsPaid();
        order.ship();

        assertThrows(
                BusinessRuleViolationException.class,
                () -> order.cancel(clock)
        );
    }

    @Test
    void shouldAddHistoryEntryWhenOrderIsCancelled() {

        Clock clock = Clock.fixed(
                Instant.parse("2026-01-01T10:00:00Z"),
                ZoneOffset.UTC
        );
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();
        order.cancel(clock);

        assertEquals(3, order.getStatusHistory().size());
        assertEquals(OrderStatus.CONFIRMED, order.getStatusHistory().get(2).getPreviousStatus());
        assertEquals(OrderStatus.CANCELLED, order.getStatusHistory().get(2).getStatus());
    }


    @Test
    void shouldShipPaidOrder() {
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();
        order.markAsPaid();
        order.ship();

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void shouldNotShipUnpaidOrder() {
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();

        assertThrows(
                BusinessRuleViolationException.class,
                order::ship
        );
    }

    @Test
    void shouldAddHistoryEntryWhenOrderIsShipped() {
        Order order = Order.create(UUID.randomUUID(), List.of(sampleItem()));

        order.confirm();
        order.markAsPaid();
        order.ship();

        assertEquals(OrderStatus.SHIPPED,
                order.getStatusHistory().get(3).getStatus());
    }


    private OrderItem sampleItem() {
        return new OrderItem(
                UUID.randomUUID(),
                1,
                new Money(BigDecimal.TEN)
        );
    }
}

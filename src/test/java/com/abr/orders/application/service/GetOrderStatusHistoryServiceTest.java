package com.abr.orders.application.service;

import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.*;
import com.abr.orders.domain.ports.out.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetOrderStatusHistoryServiceTest {

    private OrderRepository orderRepository;
    private GetOrderStatusHistoryService service;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        service = new GetOrderStatusHistoryService(orderRepository);
    }

    @Test
    void shouldReturnOrderStatusHistoryWhenOrderExists() {

        UUID orderId = UUID.randomUUID();
        Instant now = Instant.now();

        List<OrderStatusHistoryEntry> history = List.of(
                new OrderStatusHistoryEntry(
                        OrderStatus.CREATED,
                        null,
                        now.minusSeconds(60)
                ),
                new OrderStatusHistoryEntry(
                        OrderStatus.CONFIRMED,
                        OrderStatus.CREATED,
                        now
                )
        );

        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                List.of(sampleItem()),
                OrderStatus.CONFIRMED,
                now.minusSeconds(120),
                history
        );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        List<OrderStatusHistoryEntry> result =
                service.getHistory(orderId);

        assertEquals(2, result.size());
        assertEquals(OrderStatus.CREATED, result.get(0).getStatus());
        assertEquals(OrderStatus.CONFIRMED, result.get(1).getStatus());

        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> service.getHistory(orderId)
        );

        verify(orderRepository).findById(orderId);
    }

    private OrderItem sampleItem() {
        return new OrderItem(
                UUID.randomUUID(),
                1,
                new Money(java.math.BigDecimal.TEN)
        );
    }
}

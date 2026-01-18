package com.abr.orders.application.service;

import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.Money;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;
import com.abr.orders.domain.ports.out.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetOrderServiceTest {

    private OrderRepository orderRepository;
    private GetOrderService service;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        service = new GetOrderService(orderRepository);
    }

    @Test
    void shouldReturnOrderWhenExists() {

        UUID orderId = UUID.randomUUID();
        Order expectedOrder = Order.create(
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(),
                        1,
                        new Money(BigDecimal.TEN)))
        );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(expectedOrder));

        Order actualOrder = service.getById(orderId);

        assertNotNull(actualOrder);
        assertEquals(expectedOrder, actualOrder);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> service.getById(orderId)
        );

        verify(orderRepository).findById(orderId);
    }
}

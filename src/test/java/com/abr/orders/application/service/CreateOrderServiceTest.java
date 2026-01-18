package com.abr.orders.application.service;

import com.abr.orders.domain.exception.BusinessRuleViolationException;
import com.abr.orders.domain.model.*;
import com.abr.orders.domain.ports.out.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateOrderServiceTest {

    private OrderRepository orderRepository;
    private CreateOrderService service;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        service = new CreateOrderService(orderRepository);
    }

    @Test
    void shouldCreateOrderSuccessfully() {

        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = List.of(
                new OrderItem(UUID.randomUUID(),
                        2,
                        new Money(new BigDecimal("10")))
        );

        //Because of CreateOrderService make orderRepository.save, we configure mock for return everything
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order createdOrder = service.create(customerId, items);

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals(customerId, createdOrder.getCustomerId());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldNotPersistOrderWhenDomainRejectsCreation() {

        UUID customerId = UUID.randomUUID();
        List<OrderItem> emptyItems = List.of(); // Empty list.

        // Creating order with an empty list of OrderItem.
        assertThrows(
                BusinessRuleViolationException.class,
                () -> service.create(customerId, emptyItems)
        );

        verify(orderRepository, never()).save(any());
    }
}
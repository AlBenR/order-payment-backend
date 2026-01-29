package com.abr.orders.application.service;

import com.abr.orders.domain.exception.BusinessRuleViolationException;
import com.abr.orders.domain.model.*;
import com.abr.orders.domain.ports.out.OrderRepository;
import com.abr.shared.application.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
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

        AuthenticatedUser user =
                new AuthenticatedUser(
                        UUID.randomUUID(),
                        "AuthUser",
                        Set.of("CUSTOMER")
                );

        List<OrderItem> items = List.of(
                new OrderItem(
                        UUID.randomUUID(),
                        2,
                        new Money(new BigDecimal("10"))
                )
        );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        Order createdOrder = service.create(user, items);

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals(user.userId(), createdOrder.getCustomerId());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldNotPersistOrderWhenDomainRejectsCreation() {

        AuthenticatedUser user =
                new AuthenticatedUser(
                        UUID.randomUUID(),
                        "AuthUser",
                        Set.of("CUSTOMER")
                );

        List<OrderItem> emptyItems = List.of();

        assertThrows(
                BusinessRuleViolationException.class,
                () -> service.create(user,emptyItems)
        );

        verify(orderRepository, never()).save(any());
    }
}
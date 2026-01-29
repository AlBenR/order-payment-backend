package com.abr.orders.application.service;

import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.Money;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;
import com.abr.orders.domain.ports.out.OrderRepository;
import com.abr.shared.application.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        UUID customerId = UUID.randomUUID();

        Order expectedOrder = Order.create(
                customerId,
                List.of(new OrderItem(
                        UUID.randomUUID(),
                        1,
                        new Money(BigDecimal.TEN)
                ))
        );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(expectedOrder));

        AuthenticatedUser adminUser = TestAuthenticatedUsers.admin();

        Order actualOrder = service.getById(orderId, adminUser);

        assertNotNull(actualOrder);
        assertEquals(expectedOrder, actualOrder);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        AuthenticatedUser customer =
                TestAuthenticatedUsers.customer(customerId);

        assertThrows(
                OrderNotFoundException.class,
                () -> service.getById(orderId, customer)
        );

        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldDenyAccessWhenCustomerAccessesOtherCustomersOrder() {

        UUID orderId = UUID.randomUUID();
        UUID orderOwnerId = UUID.randomUUID();
        UUID otherCustomerId = UUID.randomUUID();

        Order order = Order.create(
                orderOwnerId,
                List.of(new OrderItem(
                        UUID.randomUUID(),
                        1,
                        new Money(BigDecimal.TEN)
                ))
        );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        AuthenticatedUser customer = TestAuthenticatedUsers.customer(otherCustomerId);

        assertThrows(
                AccessDeniedException.class,
                () -> service.getById(orderId, customer)
        );
    }

    public class TestAuthenticatedUsers {

        public static AuthenticatedUser admin() {
            return new AuthenticatedUser(
                    UUID.randomUUID(),
                    "admin",
                    Set.of("ADMIN")
            );
        }

        public static AuthenticatedUser customer(UUID customerId) {
            return new AuthenticatedUser(
                    customerId,
                    "customer",
                    Set.of("CUSTOMER")
            );
        }
    }
}

package com.abr.orders.application.service;

import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.ports.in.GetOrderUseCase;
import com.abr.orders.domain.ports.out.OrderRepository;
import com.abr.shared.application.exceptions.ForbiddenOperationException;
import com.abr.shared.application.security.AuthenticatedUser;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

public class GetOrderService implements GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order getById(UUID id, AuthenticatedUser user) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        authorize(order, user);

        return order;
    }

    private void authorize(Order order, AuthenticatedUser user) {

        if (user.hasRole("ADMIN")) {
            return;
        }

        if (!order.getCustomerId().equals(user.userId())) {
            throw new ForbiddenOperationException(
                    "You are not allowed to access this order"
            );
        }
    }
}


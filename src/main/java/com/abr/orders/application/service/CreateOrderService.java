package com.abr.orders.application.service;

import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;
import com.abr.orders.domain.ports.in.CreateOrderUseCase;
import com.abr.orders.domain.ports.out.OrderRepository;
import com.abr.shared.application.security.AuthenticatedUser;

import java.util.List;
import java.util.UUID;

public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;

    public CreateOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(AuthenticatedUser user, List<OrderItem> items) {

        UUID customerId = user.userId();

        Order order = Order.create(customerId, items);

        orderRepository.save(order);

        return order;
    }
}

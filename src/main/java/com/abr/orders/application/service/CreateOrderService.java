package com.abr.orders.application.service;

import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;
import com.abr.orders.domain.ports.in.CreateOrderUseCase;
import com.abr.orders.domain.ports.out.OrderRepository;

import java.util.List;
import java.util.UUID;

public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;

    public CreateOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(UUID costumerId, List<OrderItem> items) {

        Order order = Order.create(costumerId, items);
        return orderRepository.save(order);
    }
}

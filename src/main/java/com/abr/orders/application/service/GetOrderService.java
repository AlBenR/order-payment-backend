package com.abr.orders.application.service;

import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.ports.in.GetOrderUseCase;
import com.abr.orders.domain.ports.out.OrderRepository;

import java.util.UUID;

public class GetOrderService implements GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderNotFoundException(orderId));
    }
}


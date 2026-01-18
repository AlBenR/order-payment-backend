package com.abr.orders.application.service;

import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.OrderStatusHistoryEntry;
import com.abr.orders.domain.ports.in.GetOrderStatusHistoryUseCase;
import com.abr.orders.domain.ports.out.OrderRepository;

import java.util.List;
import java.util.UUID;

public class GetOrderStatusHistoryService
        implements GetOrderStatusHistoryUseCase {

    private final OrderRepository orderRepository;

    public GetOrderStatusHistoryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderStatusHistoryEntry> getHistory(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId))
                .getStatusHistory();
    }
}
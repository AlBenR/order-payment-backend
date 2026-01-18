package com.abr.orders.application.service;

import com.abr.orders.domain.event.DomainEvent;
import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.ports.in.ConfirmOrderUseCase;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.orders.domain.ports.out.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;

    public ConfirmOrderService(OrderRepository orderRepository, DomainEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Override
    public void confirm(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderNotFoundException(orderId));

        order.confirm();
        orderRepository.save(order);

        List<DomainEvent> events = order.pullDomainEvents();
        eventPublisher.publishAll(events);
    }
}

package com.abr.orders.application.service;

import com.abr.orders.domain.event.DomainEvent;
import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.ports.in.CancelOrderUseCase;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.orders.domain.ports.out.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;
    private final Clock clock;

    public CancelOrderService(OrderRepository orderRepository, DomainEventPublisher eventPublisher,
                              Clock clock) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    @Override
    public void cancel(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderNotFoundException(orderId));

        order.cancel(clock);

        orderRepository.save(order);
        List<DomainEvent> events = order.pullDomainEvents();
        eventPublisher.publishAll(events);
    }
}

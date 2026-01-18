package com.abr.orders.application.service;

import com.abr.orders.application.exception.ConcurrentOrderModificationException;
import com.abr.orders.domain.event.DomainEvent;
import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.IdempotencyKey;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.ports.in.PayOrderUseCase;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.orders.domain.ports.out.OrderRepository;
import com.abr.orders.domain.ports.out.ProcessedPaymentRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class PayOrderService implements PayOrderUseCase {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;
    private final ProcessedPaymentRepository processedPaymentRepository;

    public PayOrderService (OrderRepository orderRepository, DomainEventPublisher eventPublisher,
                            ProcessedPaymentRepository processedPaymentRepository){
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.processedPaymentRepository = processedPaymentRepository;
    }

    @Transactional
    @Override
    public void pay(UUID orderId, IdempotencyKey key) {

        if (processedPaymentRepository.existsByKey(key)) {
            return;
        }

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));

            order.markAsPaid();

            orderRepository.save(order);
            processedPaymentRepository.save(orderId, key);

            List<DomainEvent> events = order.pullDomainEvents();
            eventPublisher.publishAll(events);

        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ConcurrentOrderModificationException();
        }
    }
}


package com.abr.orders.application.service;

import com.abr.orders.domain.model.IdempotencyKey;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderStatus;
import com.abr.orders.domain.ports.out.OrderRepository;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.orders.domain.ports.out.ProcessedPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MarkOrderAsPaidService {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;
    private final ProcessedPaymentRepository processedPaymentRepository;

    public MarkOrderAsPaidService(
            OrderRepository orderRepository,
            DomainEventPublisher eventPublisher,
            ProcessedPaymentRepository processedPaymentRepository
    ) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.processedPaymentRepository = processedPaymentRepository;
    }

    @Transactional
    public void markAsPaid(UUID orderId, String eventId) {

        IdempotencyKey key = new IdempotencyKey(eventId);

        if (processedPaymentRepository.existsByKey(key)) {
            return;
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Order not found: " + orderId
                        )
                );

        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }

        order.markAsPaid();

        orderRepository.save(order);
        processedPaymentRepository.save(orderId, key);
        eventPublisher.publishAll(order.pullDomainEvents());

    }
}

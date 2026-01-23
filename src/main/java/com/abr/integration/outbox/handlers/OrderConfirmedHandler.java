package com.abr.integration.outbox.handlers;

import com.abr.integration.outbox.OutboxEventHandler;
import com.abr.orders.domain.event.OrderConfirmedEvent;
import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import com.abr.payment.aplication.service.CreatePaymentFromOrderService;
import com.abr.payment.domain.model.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedHandler implements OutboxEventHandler {

    private final ObjectMapper objectMapper;
    private final CreatePaymentFromOrderService service;

    public OrderConfirmedHandler(
            ObjectMapper objectMapper,
            CreatePaymentFromOrderService service
    ) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @Override
    public boolean supports(OutboxEventEntity event) {
        return "Order".equals(event.getAggregateType())
                && "OrderConfirmedEvent".equals(event.getEventType());
    }

    @Override
    public void handle(OutboxEventEntity event) {

        try {
            OrderConfirmedEvent confirmed =
                    objectMapper.readValue(
                            event.getPayload(),
                            OrderConfirmedEvent.class
                    );

            service.createFromOrder(
                    confirmed.getOrderId(),
                    new Money(confirmed.getTotalAmount()));

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to handle OrderConfirmedEvent", e
            );
        }
    }
}


package com.abr.integration.outbox.handlers;

import com.abr.integration.outbox.OutboxEventHandler;
import com.abr.orders.application.service.MarkOrderAsPaidService;
import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import com.abr.payment.domain.event.PaymentCreated;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentCreatedHandler implements OutboxEventHandler {

    private final ObjectMapper objectMapper;
    private final MarkOrderAsPaidService service;

    public PaymentCreatedHandler(
            ObjectMapper objectMapper,
            MarkOrderAsPaidService service
    ) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @Override
    public boolean supports(OutboxEventEntity event) {
        return "Payment".equals(event.getAggregateType())
                && "PaymentCreated".equals(event.getEventType());
    }

    @Override
    public void handle(OutboxEventEntity event) {
        try {
            PaymentCreated paymentCreated =
                    objectMapper.readValue(
                            event.getPayload(),
                            PaymentCreated.class
                    );

            service.markAsPaid(paymentCreated.getOrderId().value(), event.getId().toString());

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to handle PaymentCreated event", e
            );
        }
    }
}



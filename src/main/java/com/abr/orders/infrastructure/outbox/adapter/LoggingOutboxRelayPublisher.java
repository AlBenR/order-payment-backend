package com.abr.orders.infrastructure.outbox.adapter;

import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingOutboxRelayPublisher implements OutboxRelayPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingOutboxRelayPublisher.class);

    @Override
    public void publish(OutboxEventEntity event) {
        log.info(
                "Publishing outbox event [{}] for aggregate [{}]: {}",
                event.getEventType(),
                event.getAggregateId(),
                event.getPayload()
        );
    }
}
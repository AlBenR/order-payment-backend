package com.abr.integration.outbox;

import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import com.abr.orders.infrastructure.outbox.entity.OutboxStatus;
import com.abr.orders.infrastructure.outbox.repository.JpaOutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class OutboxEventProcessor {

    private final JpaOutboxEventRepository repository;
    private final List<OutboxEventHandler> handlers;

    public OutboxEventProcessor(
            JpaOutboxEventRepository repository,
            List<OutboxEventHandler> handlers
    ) {
        this.repository = repository;
        this.handlers = handlers;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void process() {

        Instant threshold = Instant.now().minus(1, ChronoUnit.SECONDS);

        List<OutboxEventEntity> events =
                repository.findPending(OutboxStatus.PENDING, 5, threshold)
                        .stream()
                        .filter(OutboxEventEntity::isInternal)
                        .toList();

        for (OutboxEventEntity event : events) {
            try {
                OutboxEventHandler handler = handlers.stream()
                        .filter(h -> h.supports(event))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No handler for event " + event.getEventType()
                                )
                        );

                handler.handle(event);
                event.markAsSent();

            } catch (Exception ex) {

                event.registerFailure();

                if (event.hasReachedMaxAttempts()) {
                    event.markAsFailed();
                }
            }
        }
    }

}


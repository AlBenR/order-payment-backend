package com.abr.orders.infrastructure.outbox.relay;

import com.abr.orders.infrastructure.outbox.adapter.OutboxRelayPublisher;
import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import com.abr.orders.infrastructure.outbox.entity.OutboxStatus;
import com.abr.orders.infrastructure.outbox.repository.JpaOutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class OutboxRelay {

    private final JpaOutboxEventRepository repository;
    private final OutboxRelayPublisher publisher;
    private final Clock clock;

    public OutboxRelay(
            JpaOutboxEventRepository repository,
            OutboxRelayPublisher publisher,
            Clock clock
    ) {
        this.repository = repository;
        this.publisher = publisher;
        this.clock = clock;
    }

    @Transactional
    public void processPendingEvents() {

        Instant now = clock.instant();

        List<OutboxEventEntity> events =
                repository.findPending(OutboxStatus.PENDING, 5);

        for (OutboxEventEntity event : events) {

            if (!event.canRetry(now)) {
                continue;
            }

            try {
                publisher.publish(event);
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

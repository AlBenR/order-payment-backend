package com.abr.orders.infrastructure.outbox.adapter;

import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;

public interface OutboxRelayPublisher {

    void publish(OutboxEventEntity event);
}

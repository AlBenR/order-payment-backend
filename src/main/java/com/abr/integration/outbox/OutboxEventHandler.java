package com.abr.integration.outbox;

import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;

public interface OutboxEventHandler {

    boolean supports(OutboxEventEntity event);

    void handle(OutboxEventEntity event);
}

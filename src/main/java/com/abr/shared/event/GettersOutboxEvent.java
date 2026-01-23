package com.abr.shared.event;

import java.util.UUID;

public interface GettersOutboxEvent {

    UUID getAggregateId();

    String getAggregateType();
}

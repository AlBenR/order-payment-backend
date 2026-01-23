package com.abr.shared.outbox;

public interface OutboxPublisher {

    void publish(Object event);
}

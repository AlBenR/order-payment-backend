package com.abr.orders.domain.ports.out;

import com.abr.orders.domain.event.DomainEvent;

import java.util.List;

public interface DomainEventPublisher {
    void publishAll(List<DomainEvent> events);
}

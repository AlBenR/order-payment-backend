package com.abr.payment.domain.ports.out;

import java.util.List;

public interface DomainEventPublisher {

    void publish(Object events);
}

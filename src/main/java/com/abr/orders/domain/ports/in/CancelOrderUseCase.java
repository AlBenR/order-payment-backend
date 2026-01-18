package com.abr.orders.domain.ports.in;

import java.util.UUID;

public interface CancelOrderUseCase {

    void cancel(UUID orderId);
}

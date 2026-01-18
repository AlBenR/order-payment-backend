package com.abr.orders.domain.ports.in;

import java.util.UUID;

public interface ShipOrderUseCase {

    void ship(UUID orderId);
}

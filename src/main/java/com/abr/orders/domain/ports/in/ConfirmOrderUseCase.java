package com.abr.orders.domain.ports.in;

import java.util.UUID;

public interface ConfirmOrderUseCase {

    void confirm(UUID orderId);
}

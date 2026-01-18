package com.abr.orders.domain.ports.in;

import com.abr.orders.domain.model.Order;

import java.util.UUID;

public interface GetOrderUseCase {

    Order getById(UUID orderId);
}

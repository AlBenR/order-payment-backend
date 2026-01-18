package com.abr.orders.domain.ports.in;

import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;

import java.util.List;
import java.util.UUID;

public interface CreateOrderUseCase {

    Order create(UUID costumerId, List<OrderItem> items);
}

package com.abr.orders.domain.ports.in;

import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;
import com.abr.shared.application.security.AuthenticatedUser;

import java.util.List;
import java.util.UUID;

public interface CreateOrderUseCase {

    Order create(AuthenticatedUser user, List<OrderItem> items);
}

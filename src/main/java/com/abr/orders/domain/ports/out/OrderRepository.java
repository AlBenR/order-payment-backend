package com.abr.orders.domain.ports.out;

import com.abr.orders.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save (Order order);

    Optional<Order> findById(UUID Id);
}

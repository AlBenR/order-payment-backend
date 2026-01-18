package com.abr.orders.domain.ports.in;

import com.abr.orders.domain.model.OrderStatusHistoryEntry;

import java.util.List;
import java.util.UUID;

public interface GetOrderStatusHistoryUseCase {
    List<OrderStatusHistoryEntry> getHistory(UUID orderId);
}

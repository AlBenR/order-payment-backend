package com.abr.orders.infrastructure.rest.mapper;

import com.abr.orders.domain.model.*;
import com.abr.orders.infrastructure.rest.dto.*;
import org.springframework.stereotype.Component;


@Component
public class OrderRestMapper {

    public OrderItem toDomain(OrderItemRequest dto) {
        return new OrderItem(
                dto.getProductId(),
                dto.getQuantity(),
                new Money(dto.getPrice())
        );
    }

    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus().name());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(
                order.getItems().stream()
                        .map(this::toItemResponse)
                        .toList()
        );
        return response;
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse r = new OrderItemResponse();
        r.setProductId(item.getProductId());
        r.setQuantity(item.getQuantity());
        r.setPrice(item.getPrice().getAmount());
        return r;
    }

    public OrderStatusHistoryResponse toHistoryResponse(
            OrderStatusHistoryEntry entry
    ) {
        OrderStatusHistoryResponse r = new OrderStatusHistoryResponse();
        r.setStatus(entry.getStatus().name());
        r.setPreviousStatus(
                entry.getPreviousStatus() != null
                        ? entry.getPreviousStatus().name()
                        : null
        );
        r.setChangedAt(entry.getChangedAt());
        return r;
    }
}


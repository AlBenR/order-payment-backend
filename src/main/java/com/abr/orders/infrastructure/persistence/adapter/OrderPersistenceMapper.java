package com.abr.orders.infrastructure.persistence.adapter;

import com.abr.orders.domain.model.*;
import com.abr.orders.infrastructure.persistence.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setCustomerId(order.getCustomerId());
        entity.setStatus(order.getStatus());
        entity.setCreatedAt(order.getCreatedAt());

        entity.setItems(
                new ArrayList<>(order.getItems()).stream()
                        .map(this::toEmbeddable)
                        .collect(Collectors.toList())
        );

        entity.setStatusHistory(
                new ArrayList<>(order.getStatusHistory()).stream()
                        .map(this::toHistoryEmbeddable)
                        .collect(Collectors.toList())
        );

        return entity;
    }

    public void updateEntity(OrderEntity entity, Order order) {

        entity.setCustomerId(order.getCustomerId());
        entity.setStatus(order.getStatus());
        entity.setCreatedAt(order.getCreatedAt());

        // items (reemplazo completo, como ya haces)
        entity.setItems(
                order.getItems().stream()
                        .map(this::toEmbeddable)
                        .toList()
        );

        // status history (igual que antes)
        entity.setStatusHistory(
                order.getStatusHistory().stream()
                        .map(this::toHistoryEmbeddable)
                        .collect(Collectors.toList())
        );
    }

    public Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getItems().stream()
                        .map(this::toDomainItem)
                        .collect(Collectors.toList()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getStatusHistory().stream()
                        .map(this::toDomainHistory)
                        .collect(Collectors.toList())

        );
    }

    // ------------------------
    // Order Items
    // ------------------------

    private OrderItemEmbeddable toEmbeddable(OrderItem item) {
        OrderItemEmbeddable emb = new OrderItemEmbeddable();
        emb.setProductId(item.getProductId());
        emb.setQuantity(item.getQuantity());
        emb.setPrice(item.getPrice().getAmount());
        return emb;
    }

    private OrderItem toDomainItem(OrderItemEmbeddable emb) {
        return new OrderItem(
                emb.getProductId(),
                emb.getQuantity(),
                new Money(emb.getPrice())
        );
    }

    // ------------------------
    // Status History
    // ------------------------

    private OrderStatusHistoryEmbeddable toHistoryEmbeddable(
            OrderStatusHistoryEntry entry
    ) {
        return new OrderStatusHistoryEmbeddable(
                entry.getStatus(),
                entry.getPreviousStatus(),
                entry.getChangedAt()
        );
    }

    private OrderStatusHistoryEntry toDomainHistory(
            OrderStatusHistoryEmbeddable emb
    ) {
        return new OrderStatusHistoryEntry(
                emb.getStatus(),
                emb.getPreviousStatus(),
                emb.getChangedAt()
        );
    }
}


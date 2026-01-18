package com.abr.orders.infrastructure.persistence.entity;

import com.abr.orders.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;

    private UUID customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;

    @Version
    private Long version;

    @ElementCollection
    @CollectionTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id")
    )
    private List<OrderItemEmbeddable> items = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "order_status_history",
            joinColumns = @JoinColumn(name = "order_id")
    )
    private List<OrderStatusHistoryEmbeddable> statusHistory = new ArrayList<>();

    public void setId(UUID id) {

        this.id = id;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setItems(List<OrderItemEmbeddable> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void setStatusHistory(List<OrderStatusHistoryEmbeddable> history) {
        this.statusHistory.clear();
        this.statusHistory.addAll(history);
    }
}

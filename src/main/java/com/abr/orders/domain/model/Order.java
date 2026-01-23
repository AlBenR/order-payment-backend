package com.abr.orders.domain.model;

import com.abr.orders.domain.event.*;
import com.abr.orders.domain.exception.BusinessRuleViolationException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final UUID customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Instant createdAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private final List<OrderStatusHistoryEntry> statusHistory = new ArrayList<>();
    private static final Duration PAID_ORDER_CANCELLATION_WINDOW = Duration.ofMinutes(10);

    //Factory method (CREATION)
    public static Order create(UUID customerId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "Order must have at least 1 item"
            );
        }

        List<OrderStatusHistoryEntry> history = new ArrayList<>();
        history.add(
                new OrderStatusHistoryEntry(
                        OrderStatus.CREATED,
                        null,
                        Instant.now()
                )
        );

        return new Order(
                UUID.randomUUID(),
                customerId,
                items,
                OrderStatus.CREATED,
                Instant.now(),
                history
        );
    }


    //Constructor (For Mapper)
    public Order(
            UUID id,
            UUID customerId,
            List<OrderItem> items,
            OrderStatus status,
            Instant createdAt,
            List<OrderStatusHistoryEntry> statusHistory
    ) {
        this.id = id;
        this.customerId = customerId;
        this.items =  new ArrayList<>(items);;
        this.status = status;
        this.createdAt = createdAt;
        this.statusHistory.addAll(statusHistory);
    }

    //Domain behavior
    public void confirm() {
        status.validateConfirm();
        OrderStatus previous = this.status;

        if (items == null || items.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "Cannot confirm an order without items"
            );
        }
        this.status = OrderStatus.CONFIRMED;
        addStatusHistory(previous, this.status);
        registerEvent(
                new OrderConfirmedEvent(
                        this.id,
                        this.totalAmount().getAmount())
        );
    }

    public void markAsPaid() {
        status.validatePay();
        OrderStatus previous = this.status;
        this.status = OrderStatus.PAID;
        addStatusHistory(previous, this.status);
        registerEvent(
                new OrderPaidEvent(
                        this.id,
                        this.totalAmount().getAmount()
                )
        );
    }

    public void cancel(Clock clock) {
        status.validateCancel();

        if (status == OrderStatus.PAID) {
            Instant paidAt = lastStatusChangeTo(OrderStatus.PAID);

            Duration elapsed =
                    Duration.between(paidAt, clock.instant());

            if (elapsed.compareTo(PAID_ORDER_CANCELLATION_WINDOW) > 0) {
                throw new BusinessRuleViolationException(
                        "Paid orders can only be canceled within 10 minutes"
                );
            }
        }

        OrderStatus previous = this.status;
        this.status = OrderStatus.CANCELLED;
        addStatusHistory(previous, this.status);
        registerEvent(new OrderCanceledEvent(this.id));
    }

    public void ship() {
        status.validateShip();
        OrderStatus previous = this.status;
        this.status = OrderStatus.SHIPPED;
        addStatusHistory(previous, this.status);
        registerEvent(
                new OrderShippedEvent(this.id));
    }

    public Money totalAmount() {
        return items.stream()
                .map(OrderItem::total)
                .reduce(new Money(java.math.BigDecimal.ZERO), Money::add);
    }

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return Collections.unmodifiableList(events);
    }

    private void addStatusHistory(OrderStatus previous, OrderStatus current) {
        this.statusHistory.add(
                new OrderStatusHistoryEntry(
                        current,
                        previous,
                        Instant.now()
                )
        );
    }

    public List<OrderStatusHistoryEntry> getStatusHistory() {
        return List.copyOf(statusHistory);
    }

    private Instant lastStatusChangeTo(OrderStatus status) {
        return statusHistory.stream()
                .filter(entry -> entry.getStatus() == status)
                .reduce((first, second) -> second)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Order was never in status " + status
                        )
                )
                .getChangedAt();
    }

    //Getters
    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

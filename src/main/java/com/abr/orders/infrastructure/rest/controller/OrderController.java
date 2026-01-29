package com.abr.orders.infrastructure.rest.controller;

import com.abr.orders.domain.model.IdempotencyKey;
import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.model.OrderItem;
import com.abr.orders.domain.model.OrderStatusHistoryEntry;
import com.abr.orders.domain.ports.in.*;
import com.abr.orders.infrastructure.rest.dto.*;
import com.abr.orders.infrastructure.rest.mapper.OrderRestMapper;

import com.abr.shared.application.security.AuthenticatedUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrder;
    private final ConfirmOrderUseCase confirmOrder;
    private final PayOrderUseCase payOrder;
    private final ShipOrderUseCase shipOrder;
    private final CancelOrderUseCase cancelOrder;
    private final GetOrderUseCase getOrder;
    private final OrderRestMapper mapper;
    private final GetOrderStatusHistoryUseCase getOrderStatusHistoryUseCase;

    public OrderController(
            CreateOrderUseCase createOrder,
            ConfirmOrderUseCase confirmOrder,
            PayOrderUseCase payOrder,
            ShipOrderUseCase shipOrder,
            CancelOrderUseCase cancelOrder,
            GetOrderUseCase getOrder,
            OrderRestMapper mapper,
            GetOrderStatusHistoryUseCase getOrderStatusHistoryUseCase
    ) {
        this.createOrder = createOrder;

        this.confirmOrder = confirmOrder;
        this.payOrder = payOrder;
        this.shipOrder = shipOrder;
        this.cancelOrder = cancelOrder;
        this.getOrder = getOrder;
        this.mapper = mapper;
        this.getOrderStatusHistoryUseCase = getOrderStatusHistoryUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        List<OrderItem> items = request.getItems().stream()
                .map(mapper::toDomain)
                .toList();

        Order order = createOrder.create(
                user,
                items
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(order));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable UUID id) {
        confirmOrder.confirm(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> pay(
            @PathVariable UUID id,
            @RequestHeader("Idempotency-Key") String rawKey
    ) {
        IdempotencyKey key = new IdempotencyKey(rawKey);
        payOrder.pay(id, key);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/ship")
    public ResponseEntity<Void> ship(@PathVariable UUID id) {
        shipOrder.ship(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        cancelOrder.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                mapper.toResponse(getOrder.getById(id, user))
        );
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<OrderStatusHistoryListResponse> history(
            @PathVariable UUID id
    ) {
        List<OrderStatusHistoryEntry> history =
                getOrderStatusHistoryUseCase.getHistory(id);

        OrderStatusHistoryListResponse response =
                new OrderStatusHistoryListResponse();
        response.setOrderId(id);
        response.setHistory(
                history.stream()
                        .map(mapper::toHistoryResponse)
                        .toList()
        );

        return ResponseEntity.ok(response);
    }

}

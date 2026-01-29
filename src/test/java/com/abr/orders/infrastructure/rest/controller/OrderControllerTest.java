package com.abr.orders.infrastructure.rest.controller;

import com.abr.orders.application.exception.ConcurrentOrderModificationException;
import com.abr.orders.domain.exception.BusinessRuleViolationException;
import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.*;
import com.abr.orders.domain.ports.in.*;
import com.abr.orders.infrastructure.rest.dto.OrderResponse;
import com.abr.orders.infrastructure.rest.dto.OrderStatusHistoryResponse;
import com.abr.orders.infrastructure.rest.mapper.OrderRestMapper;
import com.abr.shared.application.security.AuthenticatedUser;
import com.abr.shared.testsecurity.TestSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CreateOrderUseCase createOrderUseCase;
    @MockBean private ConfirmOrderUseCase confirmOrderUseCase;
    @MockBean private GetOrderUseCase getOrderUseCase;
    @MockBean private PayOrderUseCase payOrderUseCase;
    @MockBean private GetOrderStatusHistoryUseCase getOrderStatusHistoryUseCase;
    @MockBean private OrderRestMapper orderRestMapper;

    @AfterEach
    void clearSecurity() {
        TestSecurity.clear();
    }

    // ---------- CREATE ----------
    @Test
    void shouldCreateOrderSuccessfully() throws Exception {

        UUID customerId = UUID.randomUUID();
        TestSecurity.authenticateAsCustomer(customerId);

        Order order = Order.create(
                customerId,
                List.of(new OrderItem(
                        UUID.randomUUID(),
                        2,
                        new Money(BigDecimal.TEN)
                ))
        );

        when(createOrderUseCase.create(any(AuthenticatedUser.class), anyList()))
                .thenReturn(order);

        when(orderRestMapper.toResponse(order))
                .thenReturn(new OrderResponse(
                        order.getId(),
                        order.getCustomerId(),
                        order.getStatus().name(),
                        order.getCreatedAt(),
                        List.of()
                ));

        String requestJson = """
            {
              "items": [
                {
                  "productId": "%s",
                  "quantity": 2,
                  "price": 10.00
                }
              ]
            }
        """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateViolatesBusinessRule() throws Exception {

        UUID customerId = UUID.randomUUID();
        TestSecurity.authenticateAsCustomer(customerId);

        when(createOrderUseCase.create(any(AuthenticatedUser.class), anyList()))
                .thenThrow(new BusinessRuleViolationException("Order must have items"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ---------- CONFIRM ----------
    @Test
    void shouldConfirmOrderSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();
        TestSecurity.authenticateAsCustomer(UUID.randomUUID());

        doNothing().when(confirmOrderUseCase).confirm(orderId);

        mockMvc.perform(post("/orders/{id}/confirm", orderId))
                .andExpect(status().isNoContent());

        verify(confirmOrderUseCase).confirm(orderId);
    }

    // ---------- PAY ----------
    @Test
    void shouldPayOrderSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();
        TestSecurity.authenticateAsCustomer(UUID.randomUUID());

        doNothing().when(payOrderUseCase)
                .pay(eq(orderId), any(IdempotencyKey.class));

        mockMvc.perform(post("/orders/{id}/pay", orderId)
                        .header("Idempotency-Key", "pay-123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnConflictWhenConcurrentModificationOccurs() throws Exception {

        UUID orderId = UUID.randomUUID();
        TestSecurity.authenticateAsCustomer(UUID.randomUUID());

        doThrow(new ConcurrentOrderModificationException())
                .when(payOrderUseCase)
                .pay(eq(orderId), any());

        mockMvc.perform(post("/orders/{id}/pay", orderId)
                        .header("Idempotency-Key", "pay-123"))
                .andExpect(status().isConflict());
    }

    // ---------- GET ----------
    @Test
    void shouldReturnOrderWhenExists() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        TestSecurity.authenticateAsCustomer(customerId);

        Order order = Order.create(
                customerId,
                List.of(new OrderItem(
                        UUID.randomUUID(),
                        1,
                        new Money(BigDecimal.TEN)
                ))
        );

        when(getOrderUseCase.getById(eq(orderId), any()))
                .thenReturn(order);

        when(orderRestMapper.toResponse(order))
                .thenReturn(new OrderResponse(
                        order.getId(),
                        order.getCustomerId(),
                        order.getStatus().name(),
                        order.getCreatedAt(),
                        List.of()
                ));

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        TestSecurity.authenticateAsCustomer(customerId);

        when(getOrderUseCase.getById(eq(orderId), any(AuthenticatedUser.class)))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ---------- HISTORY ----------
    @Test
    void shouldReturnOrderStatusHistory() throws Exception {

        UUID orderId = UUID.randomUUID();
        TestSecurity.authenticateAsCustomer(UUID.randomUUID());

        Instant now = Instant.now();

        OrderStatusHistoryEntry entry =
                new OrderStatusHistoryEntry(
                        OrderStatus.CREATED,
                        null,
                        now
                );

        when(getOrderStatusHistoryUseCase.getHistory(orderId))
                .thenReturn(List.of(entry));

        when(orderRestMapper.toHistoryResponse(entry))
                .thenReturn(historyResponse("CREATED", null, now));

        mockMvc.perform(get("/orders/{id}/history", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.history[0].status").value("CREATED"));
    }

    private static OrderStatusHistoryResponse historyResponse(
            String status,
            String previousStatus,
            Instant changedAt
    ) {
        OrderStatusHistoryResponse r = new OrderStatusHistoryResponse();
        r.setStatus(status);
        r.setPreviousStatus(previousStatus);
        r.setChangedAt(changedAt);
        return r;
    }
}

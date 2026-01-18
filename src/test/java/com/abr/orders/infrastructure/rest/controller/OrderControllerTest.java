package com.abr.orders.infrastructure.rest.controller;

import com.abr.orders.application.exception.ConcurrentOrderModificationException;
import com.abr.orders.domain.exception.BusinessRuleViolationException;
import com.abr.orders.domain.exception.OrderNotFoundException;
import com.abr.orders.domain.model.*;
import com.abr.orders.domain.ports.in.*;
import com.abr.orders.infrastructure.rest.dto.OrderResponse;
import com.abr.orders.infrastructure.rest.dto.OrderStatusHistoryResponse;
import com.abr.orders.infrastructure.rest.mapper.OrderRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private ConfirmOrderUseCase confirmOrderUseCase;

    @MockBean
    private CancelOrderUseCase cancelOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    @MockBean
    private PayOrderUseCase payOrderUseCase;

    @MockBean
    private ShipOrderUseCase shipOrderUseCase;

    @MockBean
    private GetOrderStatusHistoryUseCase getOrderStatusHistoryUseCase;

    @MockBean
    private OrderRestMapper orderRestMapper;

    // ---- Create ----
    @Test
    void shouldCreateOrderSuccessfully() throws Exception {

        UUID customerId = UUID.randomUUID();

        Order order = Order.create(
                customerId,
                List.of(
                        new OrderItem(
                                UUID.randomUUID(),
                                2,
                                new Money(BigDecimal.TEN)
                        )
                )
        );

        when(createOrderUseCase.create(eq(customerId), anyList()))
                .thenReturn(order);

        String requestJson = """
            {
              "customerId": "%s",
              "items": [
                {
                  "productId": "%s",
                  "quantity": 2,
                  "price": 10.00
                }
              ]
            }
        """.formatted(customerId, UUID.randomUUID());

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus().name());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(List.of());

        when(orderRestMapper.toDomain(any()))
                .thenCallRealMethod();

        when(orderRestMapper.toResponse(order))
                .thenReturn(response);


        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessRuleIsViolated() throws Exception {

        UUID customerId = UUID.randomUUID();

        when(createOrderUseCase.create(eq(customerId), anyList()))
                .thenThrow(new BusinessRuleViolationException("Order must have items"));

        String requestJson = """
        {
          "customerId": "%s",
          "items": []
        }
    """.formatted(customerId);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ---- Confirm ----
    @Test
    void shouldConfirmOrderSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();

        doNothing().when(confirmOrderUseCase).confirm(orderId);

        mockMvc.perform(
                        post("/orders/{id}/confirm", orderId)
                )
                .andExpect(status().isNoContent());

        verify(confirmOrderUseCase).confirm(orderId);
    }

    @Test
    void shouldReturnBadRequestWhenConfirmingInvalidOrder() throws Exception {

        UUID orderId = UUID.randomUUID();

        doThrow(new BusinessRuleViolationException("Only CREATED orders can be confirmed"))
                .when(confirmOrderUseCase)
                .confirm(orderId);

        mockMvc.perform(post("/orders/{id}/confirm", orderId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    // ---- Pay ----
    @Test
    void shouldPayOrderSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();

        doNothing().when(payOrderUseCase)
                .pay(eq(orderId), any(IdempotencyKey.class));

        mockMvc.perform(
                        post("/orders/{id}/pay", orderId)
                                .header("Idempotency-Key", "abc-123")
                )
                .andExpect(status().isNoContent());

        verify(payOrderUseCase)
                .pay(eq(orderId), any(IdempotencyKey.class));
    }

    @Test
    void shouldReturnBadRequestWhenPayingInvalidOrder() throws Exception {

        UUID orderId = UUID.randomUUID();

        doThrow(new BusinessRuleViolationException(
                "Only CONFIRMED orders can be paid"
        ))
                .when(payOrderUseCase)
                .pay(eq(orderId), any(IdempotencyKey.class));

        mockMvc.perform(
                        post("/orders/{id}/pay", orderId)
                                .header("Idempotency-Key", "pay-456")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnBadRequestWhenIdempotencyKeyIsMissing() throws Exception {

        UUID orderId = UUID.randomUUID();

        mockMvc.perform(
                        post("/orders/{id}/pay", orderId)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenOrderIsModifiedConcurrently() throws Exception {

        UUID orderId = UUID.randomUUID();

        doThrow(new ConcurrentOrderModificationException())
                .when(payOrderUseCase)
                .pay(eq(orderId), any());

        mockMvc.perform(
                        post("/orders/{id}/pay", orderId)
                                .header("Idempotency-Key", "abc-123")
                )
                .andExpect(status().isConflict());
    }

    // ---- Ship ----
    @Test
    void shouldShipOrderSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();

        doNothing().when(shipOrderUseCase).ship(orderId);

        mockMvc.perform(
                        post("/orders/{id}/ship", orderId)
                )
                .andExpect(status().isNoContent());

        verify(shipOrderUseCase).ship(orderId);
    }

    @Test
    void shouldReturnBadRequestWhenShippingInvalidOrder() throws Exception {

        UUID orderId = UUID.randomUUID();

        doThrow(new BusinessRuleViolationException("Only PAID orders can be shipped"))
                .when(shipOrderUseCase)
                .ship(orderId);

        mockMvc.perform(post("/orders/{id}/ship", orderId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    // ---- Cancel ----
    @Test
    void shouldCancelOrderSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();

        doNothing().when(cancelOrderUseCase).cancel(orderId);

        mockMvc.perform(
                        post("/orders/{id}/cancel", orderId)
                )
                .andExpect(status().isNoContent());

        verify(cancelOrderUseCase).cancel(orderId);
    }

    @Test
    void shouldReturnBadRequestWhenCancelingInvalidOrder() throws Exception {

        UUID orderId = UUID.randomUUID();

        doThrow(new BusinessRuleViolationException("SHIPPED orders can not be canceled"))
                .when(cancelOrderUseCase)
                .cancel(orderId);

        mockMvc.perform(post("/orders/{id}/cancel", orderId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    // ---- Get ----
    @Test
    void shouldReturnOrderWhenExists() throws Exception {

        UUID orderId = UUID.randomUUID();

        Order order = Order.create(
                UUID.randomUUID(),
                List.of(new OrderItem(
                        UUID.randomUUID(),
                        1,
                        new Money(BigDecimal.TEN)
                ))
        );

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getCreatedAt(),
                List.of()
        );

        when(getOrderUseCase.getById(orderId))
                .thenReturn(order);

        when(orderRestMapper.toResponse(order))
                .thenReturn(response);

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {

        UUID orderId = UUID.randomUUID();

        when(getOrderUseCase.getById(orderId))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(
                        get("/orders/{id}", orderId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        verify(getOrderUseCase).getById(orderId);
    }

    @Test
    void shouldReturnOrderStatusHistory() throws Exception {

        UUID orderId = UUID.randomUUID();
        Instant now = Instant.now();

        OrderStatusHistoryEntry entry1 =
                new OrderStatusHistoryEntry(
                        OrderStatus.CREATED,
                        null,
                        now.minusSeconds(60)
                );

        OrderStatusHistoryEntry entry2 =
                new OrderStatusHistoryEntry(
                        OrderStatus.CONFIRMED,
                        OrderStatus.CREATED,
                        now
                );

        when(getOrderStatusHistoryUseCase.getHistory(orderId))
                .thenReturn(List.of(entry1, entry2));

        // mapper mocks
        when(orderRestMapper.toHistoryResponse(entry1))
                .thenReturn(TestFixtures.historyResponse(
                        "CREATED", null, entry1.getChangedAt()
                ));

        when(orderRestMapper.toHistoryResponse(entry2))
                .thenReturn(TestFixtures.historyResponse(
                        "CONFIRMED", "CREATED", entry2.getChangedAt()
                ));

        mockMvc.perform(get("/orders/{id}/history", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.history").isArray())
                .andExpect(jsonPath("$.history.length()").value(2))
                .andExpect(jsonPath("$.history[0].status").value("CREATED"))
                .andExpect(jsonPath("$.history[0].previousStatus").doesNotExist())
                .andExpect(jsonPath("$.history[1].status").value("CONFIRMED"))
                .andExpect(jsonPath("$.history[1].previousStatus").value("CREATED"));
    }

    @Test
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {

        UUID orderId = UUID.randomUUID();

        when(getOrderStatusHistoryUseCase.getHistory(orderId))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get("/orders/{id}/history", orderId))
                .andExpect(status().isNotFound());
    }

    class TestFixtures {

        static OrderStatusHistoryResponse historyResponse(
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

}


package com.abr.orders.infrastructure.rest.controller;

import com.abr.orders.domain.ports.in.*;
import com.abr.orders.infrastructure.rest.mapper.OrderRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(OrderController.class)
class OrderSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean CreateOrderUseCase createOrderUseCase;
    @MockBean ConfirmOrderUseCase confirmOrderUseCase;
    @MockBean CancelOrderUseCase cancelOrderUseCase;
    @MockBean GetOrderUseCase getOrderUseCase;
    @MockBean PayOrderUseCase payOrderUseCase;
    @MockBean ShipOrderUseCase shipOrderUseCase;
    @MockBean GetOrderStatusHistoryUseCase getOrderStatusHistoryUseCase;
    @MockBean OrderRestMapper orderRestMapper;

    // ---------- CREATE ----------
    @Test
    void shouldRejectCreateOrderWhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowCreateOrderForCustomer() throws Exception {
        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .with(user("user").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isCreated());
    }


    // ---------- CONFIRM ----------
    @Test
    void shouldAllowConfirmForCustomer() throws Exception {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(post("/orders/{id}/confirm", orderId)
                        .with(csrf())
                        .with(user("user").roles("CUSTOMER")))
                .andExpect(status().isNoContent());
    }


    @Test
    void shouldAllowConfirmForAdmin() throws Exception {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(post("/orders/{id}/confirm", orderId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }
}


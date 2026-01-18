package com.abr.orders.infrastructure.config;

import com.abr.orders.application.service.*;
import com.abr.orders.domain.ports.in.*;
import com.abr.orders.domain.ports.out.DomainEventPublisher;
import com.abr.orders.domain.ports.out.OrderRepository;


import com.abr.orders.domain.ports.out.ProcessedPaymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ApplicationConfig {

    //Use Cases (Ports IN)
    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepository orderRepository) {
        return new CreateOrderService(orderRepository);
    }

    @Bean
    public ConfirmOrderUseCase confirmOrderUseCase(OrderRepository orderRepository, DomainEventPublisher eventPublisher) {
        return new ConfirmOrderService(orderRepository, eventPublisher);
    }

    @Bean
    public PayOrderUseCase payOrderUseCase(OrderRepository orderRepository, DomainEventPublisher eventPublisher,
                                           ProcessedPaymentRepository processedPaymentRepository) {
        return new PayOrderService(orderRepository, eventPublisher, processedPaymentRepository);
    }

    @Bean
    public ShipOrderUseCase shipOrderUseCase(OrderRepository orderRepository, DomainEventPublisher eventPublisher) {
        return new ShipOrderService(orderRepository, eventPublisher);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(OrderRepository orderRepository, DomainEventPublisher eventPublisher, Clock clock) {
        return new CancelOrderService(orderRepository, eventPublisher, clock);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepository orderRepository) {
        return new GetOrderService(orderRepository);
    }

    @Bean
    public GetOrderStatusHistoryUseCase getOrderStatusHistoryUseCase(OrderRepository orderRepository) {
        return new GetOrderStatusHistoryService(orderRepository);
    }

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }

}

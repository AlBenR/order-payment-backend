package com.abr.orders.infrastructure.persistence.adapter;

import com.abr.orders.domain.model.Order;
import com.abr.orders.domain.ports.out.OrderRepository;
import com.abr.orders.infrastructure.persistence.entity.OrderEntity;
import com.abr.orders.infrastructure.persistence.repository.JpaOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private final JpaOrderRepository jpaRepository;
    private final OrderPersistenceMapper mapper;

    public OrderRepositoryAdapter(
            JpaOrderRepository jpaRepository,
            OrderPersistenceMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {

        OrderEntity entity;

        if (jpaRepository.existsById(order.getId())) {
            entity = jpaRepository.findById(order.getId()).orElseThrow();
            mapper.updateEntity(entity, order);
        } else {
            entity = mapper.toEntity(order);
        }

        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
}

package com.abr.orders.infrastructure.persistence.repository;

import com.abr.orders.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
}
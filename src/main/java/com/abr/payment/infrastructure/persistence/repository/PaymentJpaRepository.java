package com.abr.payment.infrastructure.persistence.repository;

import com.abr.payment.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    boolean existsByOrderId(UUID orderId);
}

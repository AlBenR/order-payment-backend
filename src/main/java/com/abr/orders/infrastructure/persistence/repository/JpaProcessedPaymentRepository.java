package com.abr.orders.infrastructure.persistence.repository;

import com.abr.orders.infrastructure.persistence.entity.ProcessedPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProcessedPaymentRepository
        extends JpaRepository<ProcessedPaymentEntity, Long> {

    boolean existsByIdempotencyKey(String idempotencyKey);
}

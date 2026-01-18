package com.abr.orders.infrastructure.persistence.adapter;

import com.abr.orders.domain.model.IdempotencyKey;
import com.abr.orders.domain.ports.out.ProcessedPaymentRepository;
import com.abr.orders.infrastructure.persistence.entity.ProcessedPaymentEntity;
import com.abr.orders.infrastructure.persistence.repository.JpaProcessedPaymentRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProcessedPaymentPersistenceAdapter
        implements ProcessedPaymentRepository {

    private final JpaProcessedPaymentRepository repository;

    public ProcessedPaymentPersistenceAdapter(
            JpaProcessedPaymentRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean existsByKey(IdempotencyKey key) {
        return repository.existsByIdempotencyKey(key.value());
    }

    @Override
    public void save(UUID orderId, IdempotencyKey key) {
        ProcessedPaymentEntity entity =
                new ProcessedPaymentEntity(orderId, key.value());
        repository.save(entity);
    }
}

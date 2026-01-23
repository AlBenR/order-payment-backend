package com.abr.orders.infrastructure.outbox.repository;

import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import com.abr.orders.infrastructure.outbox.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JpaOutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    @Query("""
    select e from OutboxEventEntity e
    where e.status = :status
      and e.attempts < :maxAttempts
      and e.occurredOn < :threshold
    """)
    List<OutboxEventEntity> findPending(
            @Param("status") OutboxStatus status,
            @Param("maxAttempts") int maxAttempts,
            @Param("threshold") Instant threshold // <--- Cambia a Instant
    );
}

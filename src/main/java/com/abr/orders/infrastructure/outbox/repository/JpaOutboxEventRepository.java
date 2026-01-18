package com.abr.orders.infrastructure.outbox.repository;

import com.abr.orders.infrastructure.outbox.entity.OutboxEventEntity;
import com.abr.orders.infrastructure.outbox.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaOutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity> findByStatus(OutboxStatus status);

    @Query("""
    select e
    from OutboxEventEntity e
    where e.status = :status
      and e.attempts < :maxAttempts
""")
    List<OutboxEventEntity> findPending(
            @Param("status") OutboxStatus status,
            @Param("maxAttempts") int maxAttempts
    );
}

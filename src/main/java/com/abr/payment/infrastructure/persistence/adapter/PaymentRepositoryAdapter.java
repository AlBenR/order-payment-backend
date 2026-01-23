package com.abr.payment.infrastructure.persistence.adapter;


import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.model.PaymentId;
import com.abr.payment.domain.ports.out.PaymentRepository;
import com.abr.payment.infrastructure.persistence.mapper.PaymentMapper;
import com.abr.payment.infrastructure.persistence.repository.PaymentJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentMapper mapper;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpaRepository, PaymentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(payment))
        );
    }

    @Override
    public Optional<Payment> findById(OrderId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrderId(OrderId orderId) {
        return jpaRepository.existsByOrderId(orderId.value());
    }

}
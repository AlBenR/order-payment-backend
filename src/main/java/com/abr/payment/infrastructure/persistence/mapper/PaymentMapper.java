package com.abr.payment.infrastructure.persistence.mapper;

import com.abr.payment.domain.model.Money;
import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.model.PaymentId;
import com.abr.payment.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentEntity toEntity(Payment payment) {
        return new PaymentEntity(
                payment.getOrderId().value(),
                payment.getAmount().getAmount(),
                payment.getStatus()
        );
    }

    public Payment toDomain(PaymentEntity entity) {
        return Payment.restore(
                new OrderId(entity.getOrderId()),
                new Money(entity.getAmount()),
                entity.getStatus()
        );
    }
}

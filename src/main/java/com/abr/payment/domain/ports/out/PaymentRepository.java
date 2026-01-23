package com.abr.payment.domain.ports.out;

import com.abr.payment.domain.model.OrderId;
import com.abr.payment.domain.model.Payment;
import com.abr.payment.domain.model.PaymentId;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(OrderId orderId);

    boolean existsByOrderId(OrderId orderId);
}

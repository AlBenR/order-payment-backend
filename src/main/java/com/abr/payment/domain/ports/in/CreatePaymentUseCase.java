package com.abr.payment.domain.ports.in;

import com.abr.payment.domain.model.OrderId;

public interface CreatePaymentUseCase {

    OrderId create(CreatePaymentCommand command);
}

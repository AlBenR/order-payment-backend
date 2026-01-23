package com.abr.payment.domain.ports.in;

public interface AuthorizePaymentUseCase {

    void authorize(AuthorizePaymentCommand command);
}

package com.abr.payment.domain.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentCommand(
        UUID orderId,
        BigDecimal amount
) {}

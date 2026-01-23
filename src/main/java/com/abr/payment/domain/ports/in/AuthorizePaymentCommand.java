package com.abr.payment.domain.ports.in;

import java.util.UUID;

public record AuthorizePaymentCommand(
        UUID orderId
) {}

package com.backend.supido.payment.domain.dto.response;

import com.backend.supido.payment.domain.enums.PaymentMethod;
import com.backend.supido.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        PaymentMethod method,
        PaymentStatus status,
        BigDecimal amount,
        String reference,
        LocalDateTime processedAt
) {}

package com.backend.supido.claim.domain.dto.response;

import com.backend.supido.claim.domain.enums.ClaimStatus;
import com.backend.supido.claim.domain.enums.ClaimType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClaimResponse(
        Long id,
        Long orderId,
        Long userId,
        ClaimType type,
        String description,
        ClaimStatus status,
        BigDecimal refundAmount,
        LocalDateTime createdAt
) {}

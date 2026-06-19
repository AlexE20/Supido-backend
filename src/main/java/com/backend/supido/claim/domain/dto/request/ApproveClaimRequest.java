package com.backend.supido.claim.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ApproveClaimRequest(

        @NotNull(message = "refundAmount is required")
        @Positive(message = "refundAmount must be a positive number")
        BigDecimal refundAmount
) {
}

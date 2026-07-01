package com.backend.supido.coupon.domain.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponRequest(

        @NotBlank(message = "code is required")
        @Size(max = 50, message = "code must not exceed 50 characters")
        String code,

        @NotNull(message = "value is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "value must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "value format invalid")
        BigDecimal value,

        @NotNull(message = "expiresAt is required")
        @Future(message = "expiresAt must be a future date")
        LocalDateTime expiresAt
) {
}

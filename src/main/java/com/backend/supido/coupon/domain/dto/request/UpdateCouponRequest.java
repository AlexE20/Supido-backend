package com.backend.supido.coupon.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateCouponRequest(

        @Size(max = 50, message = "code must not exceed 50 characters")
        String code,

        @DecimalMin(value = "0.0", inclusive = false, message = "value must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "value format invalid")
        BigDecimal value,

        @Future(message = "expiresAt must be a future date")
        LocalDateTime expiresAt,

        Boolean active

) {
}

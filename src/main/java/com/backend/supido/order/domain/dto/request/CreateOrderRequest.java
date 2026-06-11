package com.backend.supido.order.domain.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
public record CreateOrderRequest(
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be a positive number")
        Long userId,

        @NotNull(message = "restaurantId is required")
        @Positive(message = "restaurantId must be a positive number")
        Long restaurantId,

        Long couponId,

        @NotBlank(message = "deliveryAddress is required")
        String deliveryAddress,

        @DecimalMin(value = "0.0", inclusive = true, message = "tip must be >= 0")
        BigDecimal tip
) {}

package com.backend.supido.order.domain.dto.request;


import com.backend.supido.orderItem.domain.dto.request.CreateOrderItemRequest;
import com.backend.supido.payment.domain.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
        BigDecimal tip,

        @NotEmpty(message = "order must have at least one item")
        List<CreateOrderItemRequest> items,

        @NotNull(message = "paymentMethod is required")
        PaymentMethod paymentMethod
) {}

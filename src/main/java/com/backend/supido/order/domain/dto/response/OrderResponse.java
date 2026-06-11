package com.backend.supido.order.domain.dto.response;


import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderResponse(
        Long id,
        Long userId,
        Long restaurantId,
        Long deliveryPersonId,
        Long cuponId,
        String status,
        String deliveryAddress,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal discount,
        BigDecimal tip,
        BigDecimal total,
        LocalDateTime createdAt,
        LocalDateTime deliveredAt
) {
}

package com.backend.supido.order.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record UpdateOrderRequest(
        Long couponId,
        String deliveryAddress,
        String status,
        Long deliveryPersonId,
        BigDecimal tip
) {
}

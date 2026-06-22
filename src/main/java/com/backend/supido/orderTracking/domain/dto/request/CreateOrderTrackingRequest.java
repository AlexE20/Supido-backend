package com.backend.supido.orderTracking.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateOrderTrackingRequest(
        @NotNull(message = "orderId is required")
        @Positive(message = "orderId must be a positive number")
        Long orderId,

        String status,

        Double longitude,
        Double latitude,

        LocalDateTime recordedAt
) {}
package com.backend.supido.orderTracking.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderTrackingResponse(
        Long id,
        Long orderId,
        Long deliveryPersonId,
        String status,
        Double currentLatitude,
        Double currentLongitude,
        LocalDateTime estimatedDeliveryTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
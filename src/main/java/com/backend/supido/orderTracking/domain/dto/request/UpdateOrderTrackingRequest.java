package com.backend.supido.orderTracking.domain.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateOrderTrackingRequest(
        Long deliveryPersonId,
        String status,
        Double currentLatitude,
        Double currentLongitude,
        LocalDateTime estimatedDeliveryTime
) {}
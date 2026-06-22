package com.backend.supido.orderTracking.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderTrackingResponse(
        Long id,
        Long orderId,
        String status,
        Double longitude,
        Double latitude,
        LocalDateTime recordedAt
) {}
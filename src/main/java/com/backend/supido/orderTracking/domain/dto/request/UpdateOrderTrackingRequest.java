package com.backend.supido.orderTracking.domain.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateOrderTrackingRequest(
        String status,
        Double longitude,
        Double latitude,
        LocalDateTime recordedAt
) {}
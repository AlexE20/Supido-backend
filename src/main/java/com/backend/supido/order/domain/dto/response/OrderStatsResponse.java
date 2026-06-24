package com.backend.supido.order.domain.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderStatsResponse(
        double distanceKm,
        long durationSeconds,
        BigDecimal shippingCost
) {}
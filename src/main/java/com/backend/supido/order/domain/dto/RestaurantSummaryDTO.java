package com.backend.supido.order.domain.dto;

import lombok.Builder;

@Builder
public record RestaurantSummaryDTO(
        Long id,
        String name
) {}
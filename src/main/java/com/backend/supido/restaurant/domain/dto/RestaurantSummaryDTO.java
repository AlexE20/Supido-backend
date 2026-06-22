package com.backend.supido.restaurant.domain.dto;

import lombok.Builder;

@Builder
public record RestaurantSummaryDTO(
        Long id,
        String name
) {}
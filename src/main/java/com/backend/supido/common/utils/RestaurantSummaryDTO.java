package com.backend.supido.common.utils;

import lombok.Builder;

@Builder
public record RestaurantSummaryDTO(
        Long id,
        String name
) {}
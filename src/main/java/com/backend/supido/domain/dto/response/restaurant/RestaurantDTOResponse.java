package com.backend.supido.domain.dto.response.restaurant;

import lombok.Builder;

@Builder
public record RestaurantDTOResponse(
        Long id,
        String name,
        String category,
        String address,
        Double latitude,
        Double longitude,
        String openingTime,
        String closingTime,
        String photoUrl,
        Double averageRating
) {}

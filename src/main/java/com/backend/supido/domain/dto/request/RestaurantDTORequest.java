package com.backend.supido.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RestaurantDTORequest(

        @NotBlank(message = "Name is required")
        String name,

        String category,

        @NotBlank(message = "Address is required")
        String address,

        Double latitude,
        Double longitude,

        String openingTime,
        String closingTime,
        String photoUrl
) {}
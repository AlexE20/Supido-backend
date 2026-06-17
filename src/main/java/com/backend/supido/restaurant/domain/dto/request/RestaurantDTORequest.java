package com.backend.supido.restaurant.domain.dto.request;

import com.backend.supido.restaurant.common.enums.Category;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record RestaurantDTORequest(

        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must be at most 150 characters")
        String name,

        @NotNull(message = "Category is required")
        Category category,

        @NotBlank(message = "Address is required")
        @Size(max = 250, message = "Address must be at most 250 characters")
        String address,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        Double latitude,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        Double longitude,

        @NotNull(message = "Opening time is required")
        LocalTime openingTime,

        @NotNull(message = "Closing time is required")
        LocalTime closingTime,

        @Pattern(regexp = "^(https?://).*$", message = "Photo URL must be a valid URL starting with http:// or https://")
        String photoUrl
) {}
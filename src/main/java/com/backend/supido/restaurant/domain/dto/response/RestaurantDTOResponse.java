package com.backend.supido.restaurant.domain.dto.response;

import com.backend.supido.restaurant.common.enums.Category;
import lombok.Builder;
import org.springframework.cglib.core.Local;

import java.time.LocalTime;

@Builder
public record RestaurantDTOResponse(
        Long id,
        String name,
        Category category,
        String address,
        Double latitude,
        Double longitude,
        LocalTime openingTime,
        LocalTime closingTime,
        String photoUrl,
        Double averageRating,
        Boolean isOpen
) {}

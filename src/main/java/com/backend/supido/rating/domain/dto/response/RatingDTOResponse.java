package com.backend.supido.rating.domain.dto.response;

import com.backend.supido.rating.common.enums.RatingType;
import lombok.Builder;
import java.time.LocalDateTime;
import com.backend.supido.restaurant.domain.dto.RestaurantSummaryDTO;

@Builder
public record RatingDTOResponse(
        Long id,
        Long orderId,
        RestaurantSummaryDTO restaurant,
        Long ratedById,
        RatingType type,
        Integer score,
        LocalDateTime createdAt
) {}
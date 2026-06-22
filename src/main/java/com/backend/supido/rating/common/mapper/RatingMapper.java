package com.backend.supido.rating.common.mapper;

import com.backend.supido.common.utils.RestaurantSummaryDTO;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.rating.domain.dto.request.RatingDTORequest;
import com.backend.supido.rating.domain.dto.response.RatingDTOResponse;
import com.backend.supido.rating.domain.entity.Rating;

public class RatingMapper {

    public static RatingDTOResponse toResponse(Rating rating) {
        Order order = rating.getOrder();

        RestaurantSummaryDTO restaurantSummary = RestaurantSummaryDTO.builder()
                .id(order.getRestaurant().getId())
                .name(order.getRestaurant().getName())
                .build();

        return new RatingDTOResponse(
                rating.getId(),
                order.getId(),
                restaurantSummary,
                rating.getRatedById(),
                rating.getType(),
                rating.getScore(),
                rating.getCreatedAt()
        );
    }

    public static Rating toEntity(RatingDTORequest request) {
        return Rating.builder()
                .ratedById(request.ratedById())
                .type(request.type())
                .score(request.score())
                .build();
    }
}

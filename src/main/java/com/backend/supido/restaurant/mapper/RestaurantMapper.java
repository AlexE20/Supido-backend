package com.backend.supido.restaurant.mapper;

import com.backend.supido.restaurant.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.restaurant.domain.dto.response.RestaurantDTOResponse;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.common.utils.RestaurantUtils;

public class RestaurantMapper {

    public static Restaurant toEntity(RestaurantDTORequest request) {
        return Restaurant.builder()
                .name(request.name())
                .category(request.category())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .openingTime(request.openingTime())
                .closingTime(request.closingTime())
                .photoUrl(request.photoUrl())
                .build();
    }

    public static RestaurantDTOResponse toResponse(Restaurant restaurant) {
        return new RestaurantDTOResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurant.getPhotoUrl(),
                restaurant.getAverageRating(),
                RestaurantUtils.isOpen(restaurant)
        );
    }
}
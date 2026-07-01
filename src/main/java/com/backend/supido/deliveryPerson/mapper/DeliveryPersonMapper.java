package com.backend.supido.deliveryPerson.mapper;

import com.backend.supido.deliveryPerson.domain.dto.request.CreateDeliveryPersonRequest;
import com.backend.supido.deliveryPerson.domain.dto.response.DeliveryPersonResponse;
import com.backend.supido.deliveryPerson.domain.entity.DeliveryPerson;

public class DeliveryPersonMapper {

    public static DeliveryPerson toEntity(CreateDeliveryPersonRequest request) {
        return DeliveryPerson.builder()
                .userId(request.userId())
                .available(request.available() != null ? request.available() : true)
                .averageRating(0.0)
                .build();
    }

    public static DeliveryPersonResponse toDto(DeliveryPerson entity) {
        return DeliveryPersonResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .available(entity.getAvailable())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .averageRating(entity.getAverageRating())
                .lastLocationAt(entity.getLastLocationAt())
                .build();
    }
}
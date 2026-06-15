package com.backend.supido.deliveryPerson.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DeliveryPersonResponse(
        Long id,
        Long userId,
        Boolean available,
        Double latitude,
        Double longitude,
        double averageRating,
        LocalDateTime lastLocationAt
) {}
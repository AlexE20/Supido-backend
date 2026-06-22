package com.backend.supido.deliveryPerson.domain.dto;

import lombok.Builder;

@Builder
public record DeliveryPersonSummaryDTO(
        Long id,
        Long userId,
        Double averageRating
) {}

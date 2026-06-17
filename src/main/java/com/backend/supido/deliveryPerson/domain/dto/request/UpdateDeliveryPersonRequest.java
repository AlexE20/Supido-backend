package com.backend.supido.deliveryPerson.domain.dto.request;

import lombok.Builder;

@Builder
public record UpdateDeliveryPersonRequest(
        Boolean available,
        Double latitude,
        Double longitude
) {}
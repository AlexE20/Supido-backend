package com.backend.supido.deliveryPerson.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CreateDeliveryPersonRequest(
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be a positive number")
        Long userId,

        Boolean available
) {}
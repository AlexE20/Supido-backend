package com.backend.supido.rating.domain.dto.request;
import com.backend.supido.rating.common.enums.RatingType;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record RatingDTORequest(

        @NotNull(message = "Order id is required")
        Long orderId,

        @NotNull(message = "Type is required")
        RatingType type,

        @NotNull(message = "Score is required")
        @Min(value = 1, message = "Score must be at least 1")
        @Max(value = 5, message = "Score must be at most 5")
        Integer score
) {}
package com.backend.supido.claim.domain.dto.request;

import com.backend.supido.claim.domain.enums.ClaimType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateClaimRequest(

        @NotNull(message = "orderId is required")
        Long orderId,

        @NotNull(message = "userId is required")
        Long userId,

        @NotNull(message = "type is required")
        ClaimType type,

        @NotBlank(message = "description is required")
        String description
) {
}

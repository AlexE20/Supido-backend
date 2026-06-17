package com.backend.supido.menuItem.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record MenuItemDTORequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @Size(max = 250, message = "Description must be at most 250 characters")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        @Digits(integer = 6, fraction = 2, message = "Price must have at most 6 integer digits and 2 decimal places")
        BigDecimal price,

        @Pattern(regexp = "^(https?://).*$", message = "Photo URL must be a valid URL starting with http:// or https://")
        String photoUrl
) {}
package com.backend.supido.orderItem.domain.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateOrderItemRequest(
        @Min(value = 1, message = "quantity must be at least 1")
        Integer quantity,

        String notes
) { }

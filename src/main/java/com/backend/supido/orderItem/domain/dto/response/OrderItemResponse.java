package com.backend.supido.orderItem.domain.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long menuItemId,
        String menuItemName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        String notes
) {
}

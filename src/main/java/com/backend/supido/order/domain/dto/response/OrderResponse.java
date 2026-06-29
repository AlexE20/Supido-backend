package com.backend.supido.order.domain.dto.response;


import com.backend.supido.order.common.enums.Status;
import com.backend.supido.common.utils.RestaurantSummaryDTO;
import com.backend.supido.orderItem.domain.dto.response.OrderItemResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder

public record OrderResponse(
        Long id,
        Long userId,
        RestaurantSummaryDTO restaurant,
        Long deliveryPersonId,
        Long cuponId,
        Status status,
        String deliveryAddress,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal discount,
        BigDecimal tip,
        BigDecimal total,
        LocalDateTime createdAt,
        LocalDateTime deliveredAt,
        List<OrderItemResponse> items,
        Double restaurantLatitude,
        Double restaurantLongitude,
        Double deliveryLatitude,
        Double deliveryLongitude
) {
}

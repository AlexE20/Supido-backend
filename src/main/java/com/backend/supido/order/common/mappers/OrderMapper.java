package com.backend.supido.order.common.mappers;

import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderResponse;
import com.backend.supido.order.domain.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderMapper {

    public Order toEntityCreate(CreateOrderRequest request) {
        return Order.builder()
                .userId(request.userId())
                .restaurantId(request.restaurantId())
                .couponId(request.couponId())
                .deliveryAddress(request.deliveryAddress())
                .tip(request.tip())
                .build();
    }

    public Order toEntityUpdate(UpdateOrderRequest request) {
        return Order.builder()
                .couponId(request.couponId())
                .deliveryAddress(request.deliveryAddress())
                .status(request.status())
                .deliveryPersonId(request.deliveryPersonId())
                .tip(request.tip())
                .build();
    }

    public OrderResponse toDto(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getRestaurantId(),
                order.getDeliveryPersonId(),
                order.getCouponId(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getSubtotal(),
                order.getShippingCost(),
                order.getDiscount(),
                order.getTip(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getDeliveredAt()
        );
    }
}

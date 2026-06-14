package com.backend.supido.order.common.mappers;

import com.backend.supido.order.domain.dto.RestaurantSummaryDTO;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderResponse;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderMapper {

    public static Order toEntityCreate(CreateOrderRequest request, Restaurant restaurant) {
        return Order.builder()
                .userId(request.userId())
                .restaurant(restaurant)
                .couponId(request.couponId())
                .deliveryAddress(request.deliveryAddress())
                .tip(request.tip())
                .build();

    }

    public static Order toEntityUpdate(UpdateOrderRequest request) {
        return Order.builder()
                .couponId(request.couponId())
                .deliveryAddress(request.deliveryAddress())
                .status(request.status())
                .deliveryPersonId(request.deliveryPersonId())
                .tip(request.tip())
                .build();
    }

    public static OrderResponse toDto(Order order) {
        Restaurant r = order.getRestaurant();
        RestaurantSummaryDTO restaurantSummary = RestaurantSummaryDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .build();
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                restaurantSummary,
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

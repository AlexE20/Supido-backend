package com.backend.supido.order.common.mappers;

import com.backend.supido.common.utils.RestaurantSummaryDTO;
import com.backend.supido.common.utils.RestaurantUtils;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderResponse;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.orderItem.domain.dto.response.OrderItemResponse;
import com.backend.supido.orderItem.mapper.OrderItemMapper;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.userAddress.domain.entity.UserAddress;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public static Order toEntityCreate(CreateOrderRequest request, Restaurant restaurant, UserAddress userAddress, User user) {
        return Order.builder()
                .user(user)
                .restaurant(restaurant)
                .couponId(request.couponId())
                .userAddress(userAddress)
                .deliveryAddress(userAddress.getCity() + " " + userAddress.getStreet())
                .tip(request.tip())
                .build();

    }

    public static Order toEntityUpdate(UpdateOrderRequest request) {
        return Order.builder()
                .couponId(request.couponId())
                .deliveryAddress(request.deliveryAddress())
                .status(request.status())
                .tip(request.tip())
                .build();
    }

    public static OrderResponse toDto(Order order) {
        Restaurant r = order.getRestaurant();
        RestaurantSummaryDTO restaurantSummary = RestaurantSummaryDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .build();

        List<OrderItemResponse> items = order.getItems() != null ?
                order.getItems().stream()
                .map(OrderItemMapper::toDto)
                .collect(Collectors.toList())
                : List.of();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                restaurantSummary,
                order.getDeliveryPerson() != null ? order.getDeliveryPerson().getId() : null,
                order.getCouponId(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getSubtotal(),
                order.getShippingCost(),
                order.getDiscount(),
                order.getTip(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getDeliveredAt(),
                items,
                null, null, null, null
        );
    }

    public static OrderResponse toDtoForDelivery(Order order) {
        Restaurant r = order.getRestaurant();
        RestaurantSummaryDTO restaurantSummary = RestaurantSummaryDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .build();

        List<OrderItemResponse> items = order.getItems() != null ?
                order.getItems().stream()
                .map(OrderItemMapper::toDto)
                .collect(Collectors.toList())
                : List.of();

        UserAddress ua = order.getUserAddress();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                restaurantSummary,
                order.getDeliveryPerson() != null ? order.getDeliveryPerson().getId() : null,
                order.getCouponId(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getSubtotal(),
                order.getShippingCost(),
                order.getDiscount(),
                order.getTip(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getDeliveredAt(),
                items,
                r.getLatitude(),
                r.getLongitude(),
                ua != null ? ua.getLatitude() : null,
                ua != null ? ua.getLongitude() : null
        );
    }
}

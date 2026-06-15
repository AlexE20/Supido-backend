package com.backend.supido.orderTracking.mapper;

import com.backend.supido.orderTracking.domain.dto.request.CreateOrderTrackingRequest;
import com.backend.supido.orderTracking.domain.dto.response.OrderTrackingResponse;
import com.backend.supido.orderTracking.domain.entity.OrderTracking;

import java.time.LocalDateTime;

public class OrderTrackingMapper {

    public static OrderTracking toEntity(CreateOrderTrackingRequest request) {
        return OrderTracking.builder()
                .orderId(request.orderId())
                .deliveryPersonId(request.deliveryPersonId())
                .status(request.status() != null ? request.status() : "PENDING")
                .currentLatitude(request.currentLatitude())
                .currentLongitude(request.currentLongitude())
                .estimatedDeliveryTime(request.estimatedDeliveryTime())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static OrderTrackingResponse toDto(OrderTracking entity) {
        return OrderTrackingResponse.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .deliveryPersonId(entity.getDeliveryPersonId())
                .status(entity.getStatus())
                .currentLatitude(entity.getCurrentLatitude())
                .currentLongitude(entity.getCurrentLongitude())
                .estimatedDeliveryTime(entity.getEstimatedDeliveryTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
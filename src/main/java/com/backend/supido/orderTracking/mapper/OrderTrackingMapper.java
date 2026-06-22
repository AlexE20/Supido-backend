package com.backend.supido.orderTracking.mapper;

import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.orderTracking.domain.dto.request.CreateOrderTrackingRequest;
import com.backend.supido.orderTracking.domain.dto.response.OrderTrackingResponse;
import com.backend.supido.orderTracking.domain.entity.OrderTracking;

import java.time.LocalDateTime;

public class OrderTrackingMapper {

    public static OrderTracking toEntity(CreateOrderTrackingRequest request, Order order) {
        return OrderTracking.builder()
                .order(order)
                .status(request.status() != null ? request.status() : "PENDING")
                .longitude(request.longitude())
                .latitude(request.latitude())
                .recordedAt(request.recordedAt() != null ? request.recordedAt() : LocalDateTime.now())
                .build();
    }

    public static OrderTrackingResponse toDto(OrderTracking entity) {
        return OrderTrackingResponse.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .status(entity.getStatus())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .recordedAt(entity.getRecordedAt())
                .build();
    }
}
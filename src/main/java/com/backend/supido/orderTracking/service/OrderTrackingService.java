package com.backend.supido.orderTracking.service;

import com.backend.supido.orderTracking.domain.dto.request.CreateOrderTrackingRequest;
import com.backend.supido.orderTracking.domain.dto.request.UpdateOrderTrackingRequest;
import com.backend.supido.orderTracking.domain.dto.response.OrderTrackingResponse;

import java.util.List;

public interface OrderTrackingService {
    OrderTrackingResponse create(CreateOrderTrackingRequest request);
    OrderTrackingResponse findById(Long id);
    OrderTrackingResponse findByOrderId(Long orderId);
    List<OrderTrackingResponse> findAll();
    OrderTrackingResponse update(Long id, UpdateOrderTrackingRequest request);
    void delete(Long id);
}
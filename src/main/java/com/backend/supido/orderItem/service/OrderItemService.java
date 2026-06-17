package com.backend.supido.orderItem.service;

import com.backend.supido.orderItem.domain.dto.request.CreateOrderItemRequest;
import com.backend.supido.orderItem.domain.dto.request.UpdateOrderItemRequest;
import com.backend.supido.orderItem.domain.dto.response.OrderItemResponse;

import java.util.List;

public interface OrderItemService {
    List<OrderItemResponse> findAllByOrderId(Long orderId);
    OrderItemResponse findById(Long orderId, Long itemId);
    OrderItemResponse create(Long orderId, CreateOrderItemRequest request);
    OrderItemResponse update(Long orderId, Long itemId, UpdateOrderItemRequest request);
    void delete(Long orderId, Long itemId);
}

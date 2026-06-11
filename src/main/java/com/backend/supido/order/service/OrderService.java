package com.backend.supido.order.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    // CRUD básico
    OrderResponse create(CreateOrderRequest request);
    OrderResponse findById(Long id);
    PageableResponse<OrderResponse> findAll(int page, int size);
    OrderResponse update(Long id, UpdateOrderRequest request);
    void cancel(Long id);

    // Cambios de estado
    OrderResponse confirm(Long id);
    OrderResponse prepare(Long id);
    OrderResponse onTheWay(Long id);
    OrderResponse deliver(Long id);

    // Asignación
    OrderResponse assignDeliveryPerson(Long id, Long deliveryPersonId);

    // Consultas por relación
    PageableResponse<OrderResponse> findByUserId(Long userId, int page, int size);
    PageableResponse<OrderResponse> findByRestaurantId(Long restaurantId, int page, int size);
    PageableResponse<OrderResponse> findByDeliveryPersonId(Long deliveryPersonId, int page, int size);
}

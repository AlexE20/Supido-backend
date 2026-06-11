package com.backend.supido.order.service;

import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    // CRUD básico
    OrderResponse create(CreateOrderRequest request);
    OrderResponse findById(Long id);
    List<OrderResponse> findAll();
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
    List<OrderResponse> findByUserId(Long userId);
    List<OrderResponse> findByRestaurantId(Long restaurantId);
    List<OrderResponse> findByDeliveryPersonId(Long deliveryPersonId);
}

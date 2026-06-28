package com.backend.supido.order.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderReceiptResponse;
import com.backend.supido.order.domain.dto.response.OrderResponse;
import com.backend.supido.order.domain.dto.response.OrderStatsResponse;
import com.backend.supido.user.domain.entity.User;

import java.util.List;

public interface OrderService {

    // CRUD básico
    OrderResponse create(CreateOrderRequest request, User user);
    OrderResponse findById(Long id, User user);
    List<OrderResponse> findAll();
    OrderResponse update(Long id, UpdateOrderRequest request,User user);
    void cancel(Long id);

    // Cambios de estado
    OrderResponse confirm(Long id);
    OrderResponse prepare(Long id);
    OrderResponse onTheWay(Long id);
    OrderResponse deliver(Long id);

    // Asignación
    OrderResponse assignDeliveryPerson(Long id, Long deliveryPersonId);
    OrderResponse acceptOrder(Long id, User user);

    // Pago
    void confirmCashPayment(Long id, Long deliveryPersonId);
  
    // Recibo
    OrderReceiptResponse getReceipt(Long id,User user);

    // Stats
    OrderStatsResponse getOrderStats(Long id);

    // Consultas por relación
    PageableResponse<OrderResponse> findByUserId(Long userId, int page, int size);
    PageableResponse<OrderResponse> findByRestaurantId(Long restaurantId, int page, int size);
    PageableResponse<OrderResponse> findByDeliveryPersonId(Long deliveryPersonId, int page, int size, User user);
    PageableResponse<OrderResponse> findDeliveredOrdersByDeliveryPersonId(Long deliveryPersonId, int page, int size, User user);
}

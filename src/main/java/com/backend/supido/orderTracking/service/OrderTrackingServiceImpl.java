package com.backend.supido.orderTracking.service;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.orderTracking.domain.dto.request.CreateOrderTrackingRequest;
import com.backend.supido.orderTracking.domain.dto.request.UpdateOrderTrackingRequest;
import com.backend.supido.orderTracking.domain.dto.response.OrderTrackingResponse;
import com.backend.supido.orderTracking.domain.entity.OrderTracking;
import com.backend.supido.orderTracking.mapper.OrderTrackingMapper;
import com.backend.supido.orderTracking.repository.OrderTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderTrackingServiceImpl implements OrderTrackingService {

    private final OrderTrackingRepository orderTrackingRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderTrackingResponse create(CreateOrderTrackingRequest request) {
        if (!orderRepository.existsById(request.orderId())) {
            throw new ResourceNotFoundException("Order not found with id: " + request.orderId());
        }
        if (orderTrackingRepository.findByOrderId(request.orderId()).isPresent()) {
            throw new IllegalArgumentException("Tracking already exists for orderId: " + request.orderId());
        }
        return OrderTrackingMapper.toDto(orderTrackingRepository.save(OrderTrackingMapper.toEntity(request)));
    }

    @Override
    public OrderTrackingResponse findById(Long id) {
        return OrderTrackingMapper.toDto(findOrThrow(id));
    }

    @Override
    public OrderTrackingResponse findByOrderId(Long orderId) {
        return orderTrackingRepository.findByOrderId(orderId)
                .map(OrderTrackingMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("OrderTracking not found for orderId: " + orderId));
    }

    @Override
    public List<OrderTrackingResponse> findByDeliveryPersonId(Long deliveryPersonId) {
        return orderTrackingRepository.findByDeliveryPersonId(deliveryPersonId)
                .stream()
                .map(OrderTrackingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderTrackingResponse> findAll() {
        return orderTrackingRepository.findAll()
                .stream()
                .map(OrderTrackingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderTrackingResponse update(Long id, UpdateOrderTrackingRequest request) {
        OrderTracking existing = findOrThrow(id);
        if (request.deliveryPersonId() != null) existing.setDeliveryPersonId(request.deliveryPersonId());
        if (request.status() != null) existing.setStatus(request.status());
        if (request.currentLatitude() != null) existing.setCurrentLatitude(request.currentLatitude());
        if (request.currentLongitude() != null) existing.setCurrentLongitude(request.currentLongitude());
        if (request.estimatedDeliveryTime() != null) existing.setEstimatedDeliveryTime(request.estimatedDeliveryTime());
        existing.setUpdatedAt(LocalDateTime.now());
        return OrderTrackingMapper.toDto(orderTrackingRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        orderTrackingRepository.deleteById(id);
    }

    private OrderTracking findOrThrow(Long id) {
        return orderTrackingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderTracking not found with id: " + id));
    }
}
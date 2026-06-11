package com.backend.supido.order.service;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.order.common.mappers.OrderMapper;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderResponse;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.supido.order.common.mappers.OrderMapper;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse create(CreateOrderRequest request) {
        Order order = OrderMapper.toEntityCreate(request);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        return OrderMapper.toDto(saved);
    }

    @Override
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderMapper.toDto(order);
    }

    @Override
    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse update(Long id, UpdateOrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        Order updatedOrder = OrderMapper.toEntityUpdate(request);
        updatedOrder.setId(order.getId());
        updatedOrder.setUserId(order.getUserId());
        updatedOrder.setRestaurantId(order.getRestaurantId());
        updatedOrder.setSubtotal(order.getSubtotal());
        updatedOrder.setShippingCost(order.getShippingCost());
        updatedOrder.setDiscount(order.getDiscount());
        updatedOrder.setTotal(order.getTotal());
        updatedOrder.setCreatedAt(order.getCreatedAt());
        Order saved = orderRepository.save(updatedOrder);
        return OrderMapper.toDto(saved);
    }

    @Override
    public void cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (order.getStatus().equals("DELIVERED")) {
            throw new IllegalArgumentException("Cannot cancel an order that has already been delivered");
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    @Override
    public OrderResponse confirm(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Order must be in PENDING status to confirm");
        }
        order.setStatus("CONFIRMED");
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse prepare(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("CONFIRMED")) {
            throw new IllegalArgumentException("Order must be in CONFIRMED status to prepare");
        }
        order.setStatus("PREPARING");
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse onTheWay(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("PREPARING")) {
            throw new IllegalArgumentException("Order must be in PREPARING status to go on the way");
        }
        order.setStatus("ON_THE_WAY");
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse deliver(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("ON_THE_WAY")) {
            throw new IllegalArgumentException("Order must be in ON_THE_WAY status to deliver");
        }
        order.setStatus("DELIVERED");
        order.setDeliveredAt(LocalDateTime.now());
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse assignDeliveryPerson(Long id, Long deliveryPersonId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setDeliveryPersonId(deliveryPersonId);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderResponse> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> findByRestaurantId(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> findByDeliveryPersonId(Long deliveryPersonId) {
        return orderRepository.findByDeliveryPersonId(deliveryPersonId)
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }
}

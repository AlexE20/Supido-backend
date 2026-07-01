package com.backend.supido.orderItem.service;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.menuItem.domain.entity.MenuItem;
import com.backend.supido.menuItem.repository.MenuItemRepository;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.orderItem.domain.dto.request.CreateOrderItemRequest;
import com.backend.supido.orderItem.domain.dto.request.UpdateOrderItemRequest;
import com.backend.supido.orderItem.domain.dto.response.OrderItemResponse;
import com.backend.supido.orderItem.domain.entity.OrderItem;
import com.backend.supido.orderItem.mapper.OrderItemMapper;
import com.backend.supido.orderItem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;


    @Override
    public List<OrderItemResponse> findAllByOrderId(Long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(OrderItemMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public OrderItemResponse findById(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id: " + itemId + " for order: " + orderId));
        return OrderItemMapper.toDto(orderItem);

    }

    @Override
    public OrderItemResponse create(Long orderId, CreateOrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Cannot add items to an order that is not in PENDING status");
        }

        MenuItem menuItem = menuItemRepository.findById(request.menuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + request.menuItemId()));

        if (!menuItem.getAvailable()) {
            throw new IllegalArgumentException("MenuItem with id: " + request.menuItemId() + " is not available");
        }

        OrderItem orderItem = OrderItemMapper.toEntityCreate(request, order, menuItem);
        OrderItem saved = orderItemRepository.save(orderItem);

        recalculateSubtotal(order);

        return OrderItemMapper.toDto(saved);
    }

    @Override
    public OrderItemResponse update(Long orderId, Long itemId, UpdateOrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Cannot update items of an order that is not in PENDING status");
        }

        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id: " + itemId + " for order: " + orderId));

        if (request.quantity() != null) orderItem.setQuantity(request.quantity());
        if (request.notes() != null)    orderItem.setNotes(request.notes());

        OrderItem saved = orderItemRepository.save(orderItem);

        recalculateSubtotal(order);

        return OrderItemMapper.toDto(saved);
    }

    @Override
    public void delete(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Cannot delete items of an order that is not in PENDING status");
        }

        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id: " + itemId + " for order: " + orderId));

        orderItemRepository.delete(orderItem);

        recalculateSubtotal(order);

    }
    //possible util
    private void recalculateSubtotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        BigDecimal subtotal = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tip = order.getTip() != null ? order.getTip() : BigDecimal.ZERO;
        BigDecimal discount = order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO;

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTotal(subtotal.subtract(discount).add(tip));
        orderRepository.save(order);
    }
}

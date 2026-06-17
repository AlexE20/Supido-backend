package com.backend.supido.orderItem.mapper;

import com.backend.supido.menuItem.domain.entity.MenuItem;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.orderItem.domain.dto.request.CreateOrderItemRequest;
import com.backend.supido.orderItem.domain.dto.response.OrderItemResponse;
import com.backend.supido.orderItem.domain.entity.OrderItem;

import java.math.BigDecimal;

public class OrderItemMapper {

    public static OrderItem toEntityCreate(CreateOrderItemRequest request, Order order, MenuItem menuItem) {
        return OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(request.quantity())
                .unitPrice(menuItem.getPrice())
                .notes(request.notes())
                .build();
    }

    public static OrderItemResponse toDto(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getMenuItem().getId(),
                orderItem.getMenuItem().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())),
                orderItem.getNotes()
        );
    }
}

package com.backend.supido.security.order;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {
    private final OrderRepository orderRepository;

    public boolean isOwner(Authentication authentication, Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Long currentUserId;
        try {
            User currentUser = (User) authentication.getPrincipal();
            currentUserId = currentUser.getId();
        } catch (ClassCastException e) {
            return false;
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));

        if (order.getUser() == null) {
            return false;
        }

        return order.getUser().getId().equals(currentUserId);
    }}


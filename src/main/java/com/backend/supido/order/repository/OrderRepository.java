package com.backend.supido.order.repository;

import com.backend.supido.order.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByRestaurantId(Long restaurantId);
    List<Order> findByDeliveryPersonId(Long deliveryPersonId);
    List<Order> findByStatus(String status);
}

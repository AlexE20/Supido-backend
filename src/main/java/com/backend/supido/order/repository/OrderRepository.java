package com.backend.supido.order.repository;

import com.backend.supido.order.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);
    Page<Order> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable);
    List<Order> findByStatus(String status);
    List<Order> findByDeliveryPersonId(Long deliveryPersonId);
}

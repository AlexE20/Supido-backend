package com.backend.supido.orderTracking.repository;

import com.backend.supido.orderTracking.domain.entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    Optional<OrderTracking> findByOrder_Id(Long orderId);
}
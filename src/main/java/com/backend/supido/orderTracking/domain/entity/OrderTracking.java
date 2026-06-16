package com.backend.supido.orderTracking.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_tracking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orderId", unique = true, nullable = false)
    private Long orderId;

    @Column(name = "deliveryPersonId")
    private Long deliveryPersonId;

    @Column(name = "status")
    private String status;

    @Column(name = "currentLatitude")
    private Double currentLatitude;

    @Column(name = "currentLongitude")
    private Double currentLongitude;

    @Column(name = "estimatedDeliveryTime")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
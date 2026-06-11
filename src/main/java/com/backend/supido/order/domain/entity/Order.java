package com.backend.supido.order.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId")
    private long userId;

    @Column(name = "restaurantId")
    private long restaurantId;

    @Column(name = "deliveryPersonId")
    private Long deliveryPersonId;

    @Column(name = "CouponId")
    private Long couponId;

    @Column(name = "status")
    private String status;

    @Column(name = "deliveryAddress")
    private String deliveryAddress;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "shippingCost")
    private BigDecimal shippingCost;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "tip")
    private BigDecimal tip;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "deliveredAt")
    private LocalDateTime deliveredAt;
}

package com.backend.supido.order.domain.entity;

import com.backend.supido.order.common.enums.Status;
import com.backend.supido.orderItem.domain.entity.OrderItem;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.userAddress.domain.entity.UserAddress;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "deliveryPersonId")
    private Long deliveryPersonId;

    @Column(name = "CouponId")
    private Long couponId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "deliveryAddress")
    private String deliveryAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_addres_id")
    private UserAddress userAddress;

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

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items;
}

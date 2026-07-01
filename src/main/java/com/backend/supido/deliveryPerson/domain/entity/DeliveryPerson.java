package com.backend.supido.deliveryPerson.domain.entity;

import com.backend.supido.order.domain.entity.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "delivery_person")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "averageRating")
    private double averageRating;

    @Column(name = "lastLocationAt")
    private LocalDateTime lastLocationAt;

    @OneToMany(mappedBy = "deliveryPerson", fetch = FetchType.LAZY)
    private List<Order> orders;

}


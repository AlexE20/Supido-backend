package com.backend.supido.deliveryPerson.domain.entity;

import com.backend.supido.restaurant.domain.entity.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

}


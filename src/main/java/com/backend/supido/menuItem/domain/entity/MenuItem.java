package com.backend.supido.menuItem.domain.entity;

import com.backend.supido.restaurant.domain.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "available")
    private Boolean available;

    @PrePersist
    protected void onCreate() {
        if (this.available == null) this.available = true;
    }
}

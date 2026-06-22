package com.backend.supido.rating.domain.entity;

import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.rating.common.enums.RatingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"order_id", "type"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "rated_by_id", nullable = false)
    private Long ratedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RatingType type;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

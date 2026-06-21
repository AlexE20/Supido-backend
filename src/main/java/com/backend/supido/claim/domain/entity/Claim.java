package com.backend.supido.claim.domain.entity;

import com.backend.supido.claim.domain.enums.ClaimStatus;
import com.backend.supido.claim.domain.enums.ClaimType;
import com.backend.supido.order.domain.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "claims")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ClaimType type;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private ClaimStatus status;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

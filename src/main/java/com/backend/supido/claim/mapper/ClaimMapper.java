package com.backend.supido.claim.mapper;

import com.backend.supido.claim.domain.dto.request.CreateClaimRequest;
import com.backend.supido.claim.domain.dto.response.ClaimResponse;
import com.backend.supido.claim.domain.entity.Claim;
import com.backend.supido.claim.domain.enums.ClaimStatus;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.user.domain.entity.User;

import java.time.LocalDateTime;

public class ClaimMapper {

    public static Claim toEntity(CreateClaimRequest request, User user, Order order) {
        return Claim.builder()
                .order(order)
                .user(user)
                .type(request.type())
                .description(request.description())
                .status(ClaimStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ClaimResponse toDto(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getOrder().getId(),
                claim.getUser().getId(),
                claim.getType(),
                claim.getDescription(),
                claim.getStatus(),
                claim.getRefundAmount(),
                claim.getCreatedAt()
        );
    }
}

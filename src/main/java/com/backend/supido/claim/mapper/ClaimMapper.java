package com.backend.supido.claim.mapper;

import com.backend.supido.claim.domain.dto.request.CreateClaimRequest;
import com.backend.supido.claim.domain.dto.response.ClaimResponse;
import com.backend.supido.claim.domain.entity.Claim;
import com.backend.supido.claim.domain.enums.ClaimStatus;

import java.time.LocalDateTime;

public class ClaimMapper {

    public static Claim toEntity(CreateClaimRequest request) {
        return Claim.builder()
                .orderId(request.orderId())
                .userId(request.userId())
                .type(request.type())
                .description(request.description())
                .status(ClaimStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ClaimResponse toDto(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getOrderId(),
                claim.getUserId(),
                claim.getType(),
                claim.getDescription(),
                claim.getStatus(),
                claim.getRefundAmount(),
                claim.getCreatedAt()
        );
    }
}

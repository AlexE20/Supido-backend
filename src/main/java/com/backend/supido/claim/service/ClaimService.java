package com.backend.supido.claim.service;

import com.backend.supido.claim.domain.dto.request.ApproveClaimRequest;
import com.backend.supido.claim.domain.dto.request.CreateClaimRequest;
import com.backend.supido.claim.domain.dto.response.ClaimResponse;
import com.backend.supido.user.domain.entity.User;

import java.util.List;

public interface ClaimService {
    ClaimResponse create(CreateClaimRequest request, User user);
    ClaimResponse approve(Long id, ApproveClaimRequest request);
    ClaimResponse reject(Long id);
    ClaimResponse findById(Long id, User user);
    List<ClaimResponse> findByOrderId(Long orderId, User user);
    List<ClaimResponse> findByUserId(Long userId);
}

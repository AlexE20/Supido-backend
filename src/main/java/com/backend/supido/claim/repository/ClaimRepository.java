package com.backend.supido.claim.repository;

import com.backend.supido.claim.domain.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByOrderId(Long orderId);
    List<Claim> findByUserId(Long userId);
}

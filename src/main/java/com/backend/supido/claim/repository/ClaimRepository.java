package com.backend.supido.claim.repository;

import com.backend.supido.claim.domain.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByOrder_Id(Long orderId);
    List<Claim> findByUser_Id(Long userId);
}

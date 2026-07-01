package com.backend.supido.userAddress.repository;

import com.backend.supido.userAddress.domain.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findAllByUserId(Long userId);
    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);
    Optional<UserAddress> findByLabelAndUserId(String label, Long userId);
}

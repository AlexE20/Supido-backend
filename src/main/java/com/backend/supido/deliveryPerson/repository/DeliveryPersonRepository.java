package com.backend.supido.deliveryPerson.repository;

import com.backend.supido.deliveryPerson.domain.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    Optional<DeliveryPerson> findByUserId(Long userId);
    List<DeliveryPerson> findByAvailableTrue();
}
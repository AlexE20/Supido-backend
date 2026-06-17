package com.backend.supido.rating.repository;

import com.backend.supido.rating.domain.entity.Rating;
import com.backend.supido.rating.common.enums.RatingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByOrder_IdAndType(Long orderId, RatingType type);

    Page<Rating> findByOrder_Restaurant_Id(Long restaurantId, Pageable pageable);

    Page<Rating> findByRatedById(Long ratedById, Pageable pageable);
}


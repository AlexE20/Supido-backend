package com.backend.supido.rating.repository;

import com.backend.supido.rating.domain.entity.Rating;
import com.backend.supido.rating.common.enums.RatingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByOrder_IdAndType(Long orderId, RatingType type);

    List<Rating> findByOrder_Id(Long orderId);

    Page<Rating> findByOrder_Restaurant_Id(Long restaurantId, Pageable pageable);

    Page<Rating> findByRatedById(Long ratedById, Pageable pageable);
    
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.order.restaurant.id = :restaurantId AND r.type = 'RESTAURANT'")
    Double calculateAverageByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.order.deliveryPerson.id = :deliveryPersonId AND r.type = 'DELIVERY_PERSON'")
    Double calculateAverageByDeliveryPersonId(@Param("deliveryPersonId") Long deliveryPersonId);
}


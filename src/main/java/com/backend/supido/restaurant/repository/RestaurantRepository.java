package com.backend.supido.restaurant.repository;

import com.backend.supido.restaurant.domain.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Page<Restaurant> findAll(Pageable pageable);
    List<Restaurant> findByCategory(String category);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
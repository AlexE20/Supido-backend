package com.backend.supido.repositories.restaurant;

import com.backend.supido.domain.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByCategory(String category);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
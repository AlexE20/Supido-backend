package com.backend.supido.menuItem.repository;

import com.backend.supido.menuItem.domain.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);
    boolean existsByNameAndRestaurantId(String name, Long restaurantId);
    boolean existsByNameAndRestaurantIdAndIdNot(String name, Long restaurantId, Long id);
}

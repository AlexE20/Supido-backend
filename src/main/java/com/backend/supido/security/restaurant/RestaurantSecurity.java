package com.backend.supido.security.restaurant;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import com.backend.supido.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("restaurantSecurity")
@RequiredArgsConstructor
public class RestaurantSecurity {

    private final RestaurantRepository restaurantRepository;

    public boolean isOwner(Authentication authentication, Long restaurantId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Long currentUserId;
        try {
            User currentUser = (User) authentication.getPrincipal();
            currentUserId = currentUser.getId();
        } catch (ClassCastException e) {
            return false;
        }
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + restaurantId));


        if (restaurant.getUser() == null) {
            return false;
        }

        return restaurant.getUser().getId().equals(currentUserId);
    }
}
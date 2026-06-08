package com.backend.supido.services.restaurant;

import com.backend.supido.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.domain.dto.response.restaurant.RestaurantDTOResponse;
import java.util.List;

public interface RestaurantService {
    RestaurantDTOResponse createRestaurant(RestaurantDTORequest request);
    RestaurantDTOResponse findRestaurantById(Long id);
    List<RestaurantDTOResponse> findAllRestaurants();
    RestaurantDTOResponse updateRestaurant(Long id, RestaurantDTORequest request);
    void deleteRestaurant(Long id);
    List<RestaurantDTOResponse> findByCategory(String category);
    List<RestaurantDTOResponse> findByName(String name);
}

package com.backend.supido.restaurant.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.restaurant.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.restaurant.domain.dto.response.RestaurantDTOResponse;
import java.util.List;

public interface RestaurantService {
    RestaurantDTOResponse createRestaurant(RestaurantDTORequest request);
    RestaurantDTOResponse findRestaurantById(Long id);
    RestaurantDTOResponse updateRestaurant(Long id, RestaurantDTORequest request);
    void deleteRestaurant(Long id);
    List<RestaurantDTOResponse> findByCategory(String category);
    List<RestaurantDTOResponse> findByName(String name);
    PageableResponse<RestaurantDTOResponse> findAllRestaurants(int page, int size, String sortBy, String sortOrder);
}

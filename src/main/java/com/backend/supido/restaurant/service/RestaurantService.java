package com.backend.supido.restaurant.service;

import com.backend.supido.user.domain.entity.User;
import com.backend.supido.common.PageableResponse;
import com.backend.supido.restaurant.common.enums.Category;
import com.backend.supido.restaurant.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.restaurant.domain.dto.response.RestaurantDTOResponse;
import java.util.List;

public interface RestaurantService {
    RestaurantDTOResponse createRestaurant(RestaurantDTORequest request, User user);
    RestaurantDTOResponse findRestaurantById(Long id);
    RestaurantDTOResponse findMyRestaurant(User user);
    RestaurantDTOResponse updateRestaurant(Long id, RestaurantDTORequest request,User user);
    void deleteRestaurant(Long id,  User user);
    List<RestaurantDTOResponse> findByCategory(String category);
    List<RestaurantDTOResponse> findByName(String name);
    PageableResponse<RestaurantDTOResponse> findAllRestaurants(int page, int size, String sortBy, String sortOrder);
    Category[] getCategories();
}

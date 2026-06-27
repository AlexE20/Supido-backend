package com.backend.supido.restaurant.service;

import com.backend.supido.user.domain.entity.User;
import com.backend.supido.common.PageableResponse;
import com.backend.supido.restaurant.common.enums.Category;
import com.backend.supido.restaurant.common.mapper.RestaurantMapper;
import com.backend.supido.restaurant.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.restaurant.domain.dto.response.RestaurantDTOResponse;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantDTOResponse createRestaurant(RestaurantDTORequest request, User user) {
        if (restaurantRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Restaurant with name " + request.name() + " already exists");
        }
        if(restaurantRepository.existsByUserId(user.getId())){
            throw new IllegalArgumentException("Forbidden action: this user already owns a restaurant");
        }
        Restaurant restaurant = RestaurantMapper.toEntity(request,user);
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public RestaurantDTOResponse findRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + id));
        return RestaurantMapper.toResponse(restaurant);
    }

    @Override
    public PageableResponse<RestaurantDTOResponse> findAllRestaurants(int page, int size, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RestaurantDTOResponse> restaurantPage = restaurantRepository.findAll(pageable)
                .map(RestaurantMapper::toResponse);

        if (restaurantPage.getTotalElements() == 0)
            throw new ResourceNotFoundException("No restaurants found");

        return PageableResponse.<RestaurantDTOResponse>builder()
                .content(restaurantPage.getContent())
                .page(restaurantPage.getNumber())
                .size(restaurantPage.getSize())
                .totalElements(restaurantPage.getTotalElements())
                .totalPages(restaurantPage.getTotalPages())
                .last(restaurantPage.isLast())
                .build();
    }

    @Override
    public RestaurantDTOResponse updateRestaurant(Long id, RestaurantDTORequest request, User user) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + id));

        if (restaurantRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new IllegalArgumentException("Restaurant with name '" + request.name() + "' already exists");
        }

        if(!"ROLE_SUPER".equals(user.getRole().getName()) && !user.getId().equals(restaurant.getUser().getId())) {
            throw new IllegalArgumentException("This user is not allowed to update restaurant");
        }


        restaurant.setName(request.name());
        restaurant.setCategory(request.category());
        restaurant.setAddress(request.address());
        restaurant.setLatitude(request.latitude());
        restaurant.setLongitude(request.longitude());
        restaurant.setOpeningTime(request.openingTime());
        restaurant.setClosingTime(request.closingTime());
        restaurant.setPhotoUrl(request.photoUrl());

        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public void deleteRestaurant(Long id,User user) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + id));

        if(!"ROLE_SUPER".equals(user.getRole().getName()) && !user.getId().equals(restaurant.getUser().getId())) {
            throw new IllegalArgumentException("This user is not allowed to do this action");
        }

        restaurantRepository.deleteById(id);
    }

    @Override
    public List<RestaurantDTOResponse> findByCategory(String category) {
        Category categoryEnum;
        try {
            String normalized = category.trim().toUpperCase().replace(" ", "_");
            categoryEnum = Category.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        List<RestaurantDTOResponse> restaurants = restaurantRepository.findByCategory(categoryEnum)
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();

        if (restaurants.isEmpty()) {
            throw new ResourceNotFoundException("No restaurants found with category " + category);
        }

        return restaurants;
    }

    @Override
    public List<RestaurantDTOResponse> findByName(String name) {
        List<RestaurantDTOResponse> restaurants = restaurantRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();

        if (restaurants.isEmpty()) {
            throw new ResourceNotFoundException("No restaurants found with name " + name);
        }

        return restaurants;
    }

    @Override
    public Category[] getCategories() {
        return Category.values();
    }
}

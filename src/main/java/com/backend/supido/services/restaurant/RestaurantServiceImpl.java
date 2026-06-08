package com.backend.supido.services.restaurant;

import com.backend.supido.common.mappers.RestaurantMapper;
import com.backend.supido.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.domain.dto.response.restaurant.RestaurantDTOResponse;
import com.backend.supido.domain.entities.Restaurant;
import com.backend.supido.exceptions.RestaurantNotFoundException;
import com.backend.supido.repositories.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantDTOResponse createRestaurant(RestaurantDTORequest request) {
        Restaurant restaurant = RestaurantMapper.toEntity(request);
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public RestaurantDTOResponse findRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id " + id));
        return RestaurantMapper.toResponse(restaurant);
    }

    @Override
    public List<RestaurantDTOResponse> findAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();
    }

    @Override
    public RestaurantDTOResponse updateRestaurant(Long id, RestaurantDTORequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id " + id));

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
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RestaurantNotFoundException("Restaurant not found with id " + id);
        }
        restaurantRepository.deleteById(id);
    }

    @Override
    public List<RestaurantDTOResponse> findByCategory(String category) {
        return restaurantRepository.findByCategory(category)
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();
    }

    @Override
    public List<RestaurantDTOResponse> findByName(String name) {
        return restaurantRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();
    }
}

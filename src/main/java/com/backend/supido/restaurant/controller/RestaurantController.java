package com.backend.supido.restaurant.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.restaurant.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll() {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(restaurantService.findAllRestaurants())
                .message("All restaurants found")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(restaurantService.findRestaurantById(id))
                .message("Restaurant found with id: " + id)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<GeneralResponse> findByName(@RequestParam String name) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(restaurantService.findByName(name))
                .message("Restaurants found with name: " + name)
                .build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<GeneralResponse> findByCategory(@PathVariable String category) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(restaurantService.findByCategory(category))
                .message("Restaurants found with category: " + category)
                .build());
    }

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody RestaurantDTORequest request) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(restaurantService.createRestaurant(request))
                .message("Restaurant has been created")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody RestaurantDTORequest request) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(restaurantService.updateRestaurant(id, request))
                .message("Restaurant has been updated")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(null)
                .message("Restaurant has been deleted")
                .build());
    }
}
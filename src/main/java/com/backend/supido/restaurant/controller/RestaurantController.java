package com.backend.supido.restaurant.controller;

import com.backend.supido.user.domain.entity.User;
import com.backend.supido.common.GeneralResponse;
import com.backend.supido.restaurant.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        return buildResponse("All restaurants found", HttpStatus.OK,
                restaurantService.findAllRestaurants(page, size, sortBy, sortOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Restaurant found with id: " + id, HttpStatus.OK,
                restaurantService.findRestaurantById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<GeneralResponse> findByName(@RequestParam String name) {
        return buildResponse("Restaurants found with name: " + name, HttpStatus.OK,
                restaurantService.findByName(name));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<GeneralResponse> findByCategory(@PathVariable String category) {
        return buildResponse("Restaurants found with category: " + category, HttpStatus.OK,
                restaurantService.findByCategory(category));
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody RestaurantDTORequest request,@AuthenticationPrincipal User user) {
        return buildResponse("Restaurant has been created", HttpStatus.CREATED,
                restaurantService.createRestaurant(request,user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody RestaurantDTORequest request) {
        return buildResponse("Restaurant has been updated", HttpStatus.OK,
                restaurantService.updateRestaurant(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return buildResponse("Restaurant has been deleted", HttpStatus.OK, null);
    }

    public ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build()
                );
    }

    @GetMapping("/categories")
    public ResponseEntity<GeneralResponse> getCategories() {
        return buildResponse("Categories found", HttpStatus.OK, restaurantService.getCategories());
    }
}
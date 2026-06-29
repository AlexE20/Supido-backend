package com.backend.supido.rating.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.rating.domain.dto.request.RatingDTORequest;
import com.backend.supido.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody RatingDTORequest request) { //el cliente lo hace
        return buildResponse("Rating created successfully", HttpStatus.CREATED,
                ratingService.createRating(request));
    }

    @GetMapping("/{id}") //lo puede ver cliente, restaurant y el admin
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Rating found with id: " + id, HttpStatus.OK,
                ratingService.findById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> findByOrderId(@PathVariable Long orderId) {
        return buildResponse("Ratings for order: " + orderId, HttpStatus.OK,
                ratingService.findByOrderId(orderId));
    }

    @GetMapping("/restaurant/{restaurantId}")// lo puede ver el cliente, el restaurante y el admin
    public ResponseEntity<GeneralResponse> findByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return buildResponse("Ratings found for restaurant: " + restaurantId, HttpStatus.OK,
                ratingService.findByRestaurant(restaurantId, page, size, sortBy, sortOrder));
    }

    @GetMapping("/user/{ratedById}")//admin puede ver las calificaciones de todos los clientes
    public ResponseEntity<GeneralResponse> findByRatedBy(
            @PathVariable Long ratedById,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return buildResponse("Ratings found for user: " + ratedById, HttpStatus.OK,
                ratingService.findByRatedBy(ratedById, page, size, sortBy, sortOrder));
    }

    private ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
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
}
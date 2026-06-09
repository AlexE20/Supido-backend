package com.backend.supido.menuItem.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.menuItem.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.menuItem.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll(@PathVariable Long restaurantId) {
        return buildResponse("All menu items found for restaurant: " + restaurantId, HttpStatus.OK,
                menuItemService.findAllByRestaurant(restaurantId));
    }

        @GetMapping("/{id}")
        public ResponseEntity<GeneralResponse> findById(@PathVariable Long restaurantId,
                                                        @PathVariable Long id) {
            return buildResponse("Menu item found with id: " + id, HttpStatus.OK,
                    menuItemService.findMenuItemById(restaurantId, id));
        }

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@PathVariable Long restaurantId,
                                                  @Valid @RequestBody MenuItemDTORequest request) {
        return buildResponse("Menu item has been created", HttpStatus.CREATED,
                menuItemService.createMenuItem(restaurantId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long restaurantId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody MenuItemDTORequest request) {
        return buildResponse("Menu item has been updated", HttpStatus.OK,
                menuItemService.updateMenuItem(restaurantId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long restaurantId,
                                                  @PathVariable Long id) {
        menuItemService.deleteMenuItem(restaurantId, id);
        return buildResponse("Menu item has been deleted", HttpStatus.OK, null);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<GeneralResponse> toggleAvailability(@PathVariable Long restaurantId,
                                                              @PathVariable Long id) {
        return buildResponse("Menu item availability has been updated", HttpStatus.OK,
                menuItemService.toggleAvailability(restaurantId, id));
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
}
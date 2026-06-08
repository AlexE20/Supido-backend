package com.backend.supido.menuItem.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.menuItem.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.menuItem.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(menuItemService.findAllByRestaurant(restaurantId))
                .message("All menu items found for restaurant: " + restaurantId)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long restaurantId,
                                                    @PathVariable Long id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(menuItemService.findMenuItemById(id))
                .message("Menu item found with id: " + id)
                .build());
    }

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@PathVariable Long restaurantId,
                                                  @Valid @RequestBody MenuItemDTORequest request) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(menuItemService.createMenuItem(restaurantId, request))
                .message("Menu item has been created")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long restaurantId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody MenuItemDTORequest request) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(menuItemService.updateMenuItem(id, request))
                .message("Menu item has been updated")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long restaurantId,
                                                  @PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(null)
                .message("Menu item has been deleted")
                .build());
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<GeneralResponse> toggleAvailability(@PathVariable Long restaurantId,
                                                              @PathVariable Long id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(menuItemService.toggleAvailability(id))
                .message("Menu item availability has been updated")
                .build());
    }
}
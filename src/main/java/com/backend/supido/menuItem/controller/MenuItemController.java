package com.backend.supido.menuItem.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.menuItem.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.menuItem.service.MenuItemService;
import com.backend.supido.user.domain.entity.User;
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
@RequestMapping("/api/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        return buildResponse("All menu items found for restaurant: " + restaurantId, HttpStatus.OK,
                menuItemService.findAllByRestaurant(restaurantId, page, size, sortBy, sortOrder));
    }

    @PreAuthorize("hasRole('ROLE_RESTAURANT') and @restaurantSecurity.isOwner(authentication, #restaurantId)")
        @GetMapping("/{id}")
        public ResponseEntity<GeneralResponse> findById(@PathVariable Long restaurantId,
                                                        @PathVariable Long id) {
            return buildResponse("Menu item found with id: " + id, HttpStatus.OK,
                    menuItemService.findMenuItemById(restaurantId, id));
        }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RESTAURANT') and @restaurantSecurity.isOwner(authentication, #restaurantId)")
    public ResponseEntity<GeneralResponse> create(@PathVariable Long restaurantId,
                                                  @Valid @RequestBody MenuItemDTORequest request,
                                                    @AuthenticationPrincipal User user) {
        return buildResponse("Menu item has been created", HttpStatus.CREATED,
                menuItemService.createMenuItem(restaurantId, request,user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RESTAURANT') and @restaurantSecurity.isOwner(authentication, #restaurantId)")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long restaurantId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody MenuItemDTORequest request) {
        return buildResponse("Menu item has been updated", HttpStatus.OK,
                menuItemService.updateMenuItem(restaurantId, id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RESTAURANT') and @restaurantSecurity.isOwner(authentication, #restaurantId)")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long restaurantId,
                                                  @PathVariable Long id) {
        menuItemService.deleteMenuItem(restaurantId, id);
        return buildResponse("Menu item has been deleted", HttpStatus.OK, null);
    }
    @PreAuthorize("hasRole('ROLE_RESTAURANT') and @restaurantSecurity.isOwner(authentication, #restaurantId)")
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
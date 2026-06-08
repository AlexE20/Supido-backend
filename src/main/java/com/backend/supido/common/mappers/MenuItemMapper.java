package com.backend.supido.common.mappers;

import com.backend.supido.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.domain.dto.response.menuItem.MenuItemDTOResponse;
import com.backend.supido.domain.entities.MenuItem;

public class MenuItemMapper {

    public static MenuItem toEntity(MenuItemDTORequest request) {
        return MenuItem.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .photoUrl(request.photoUrl())
                .build();
    }

    public static MenuItemDTOResponse toResponse(MenuItem menuItem) {
        return new MenuItemDTOResponse(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory(),
                menuItem.getPhotoUrl(),
                menuItem.getAvailable()
        );
    }
}

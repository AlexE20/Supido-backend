package com.backend.supido.menuItem.mapper;

import com.backend.supido.menuItem.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.menuItem.domain.dto.response.MenuItemDTOResponse;
import com.backend.supido.menuItem.domain.entity.MenuItem;

public class MenuItemMapper {

    public static MenuItem toEntity(MenuItemDTORequest request) {
        return MenuItem.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .photoUrl(request.photoUrl())
                .build();
    }

    public static MenuItemDTOResponse toResponse(MenuItem menuItem) {
        return new MenuItemDTOResponse(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getPhotoUrl(),
                menuItem.getAvailable()
        );
    }
}

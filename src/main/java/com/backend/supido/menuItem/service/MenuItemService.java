package com.backend.supido.menuItem.service;


import com.backend.supido.menuItem.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.menuItem.domain.dto.response.MenuItemDTOResponse;
import java.util.List;

public interface MenuItemService {
    MenuItemDTOResponse createMenuItem(Long restaurantId, MenuItemDTORequest request);
    MenuItemDTOResponse findMenuItemById(Long id);
    List<MenuItemDTOResponse> findAllByRestaurant(Long restaurantId);
    MenuItemDTOResponse updateMenuItem(Long id, MenuItemDTORequest request);
    void deleteMenuItem(Long id);
    MenuItemDTOResponse toggleAvailability(Long id);
}

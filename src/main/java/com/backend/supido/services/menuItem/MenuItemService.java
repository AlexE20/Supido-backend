package com.backend.supido.services.menuItem;


import com.backend.supido.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.domain.dto.response.menuItem.MenuItemDTOResponse;
import java.util.List;

public interface MenuItemService {
    MenuItemDTOResponse createMenuItem(Long restaurantId, MenuItemDTORequest request);
    MenuItemDTOResponse findMenuItemById(Long id);
    List<MenuItemDTOResponse> findAllByRestaurant(Long restaurantId);
    MenuItemDTOResponse updateMenuItem(Long id, MenuItemDTORequest request);
    void deleteMenuItem(Long id);
    MenuItemDTOResponse toggleAvailability(Long id);
}

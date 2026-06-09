package com.backend.supido.menuItem.service;


import com.backend.supido.common.PageableResponse;
import com.backend.supido.menuItem.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.menuItem.domain.dto.response.MenuItemDTOResponse;
import java.util.List;

public interface MenuItemService {
    MenuItemDTOResponse createMenuItem(Long restaurantId, MenuItemDTORequest request);
    MenuItemDTOResponse findMenuItemById(Long restaurantId, Long id);
    PageableResponse<MenuItemDTOResponse> findAllByRestaurant(Long restaurantId, int page, int size, String sortBy, String sortOrder);
    MenuItemDTOResponse updateMenuItem(Long restaurantId, Long id, MenuItemDTORequest request);
    void deleteMenuItem(Long restaurantId, Long id);
    MenuItemDTOResponse toggleAvailability(Long restaurantId, Long id);
}

package com.backend.supido.menuItem.service;

import com.backend.supido.menuItem.mapper.MenuItemMapper;
import com.backend.supido.menuItem.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.menuItem.domain.dto.response.MenuItemDTOResponse;
import com.backend.supido.menuItem.domain.entity.MenuItem;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.menuItem.repository.MenuItemRepository;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public MenuItemDTOResponse createMenuItem(Long restaurantId, MenuItemDTORequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + restaurantId));

        MenuItem menuItem = MenuItemMapper.toEntity(request);
        menuItem.setRestaurant(restaurant);

        return MenuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Override
    public MenuItemDTOResponse findMenuItemById(Long restaurantId, Long id) {
        return MenuItemMapper.toResponse(findMenuItemBelongingToRestaurant(restaurantId, id));
    }


    @Override
    public List<MenuItemDTOResponse> findAllByRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id " + restaurantId);
        }
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
    }

    @Override
    public MenuItemDTOResponse updateMenuItem(Long restaurantId, Long id, MenuItemDTORequest request) {
        MenuItem menuItem = findMenuItemBelongingToRestaurant(restaurantId, id);
        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setCategory(request.category());
        menuItem.setPhotoUrl(request.photoUrl());
        return MenuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Override
    public void deleteMenuItem(Long restaurantId, Long id) {
        findMenuItemBelongingToRestaurant(restaurantId, id);
        menuItemRepository.deleteById(id);
    }

    @Override
    public MenuItemDTOResponse toggleAvailability(Long restaurantId, Long id) {
        MenuItem menuItem = findMenuItemBelongingToRestaurant(restaurantId, id);
        menuItem.setAvailable(!menuItem.getAvailable());
        return MenuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    private MenuItem findMenuItemBelongingToRestaurant(Long restaurantId, Long menuItemId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id " + restaurantId);
        }
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + menuItemId));

        if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException("Menu item " + menuItemId + " does not belong to restaurant " + restaurantId);
        }
        return menuItem;
    }
}
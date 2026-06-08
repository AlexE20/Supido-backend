package com.backend.supido.services.menuItem;

import com.backend.supido.common.mappers.MenuItemMapper;
import com.backend.supido.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.domain.dto.response.menuItem.MenuItemDTOResponse;
import com.backend.supido.domain.entities.MenuItem;
import com.backend.supido.domain.entities.Restaurant;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.repositories.menuItem.MenuItemRepository;
import com.backend.supido.repositories.restaurant.RestaurantRepository;
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
    public MenuItemDTOResponse findMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + id));
        return MenuItemMapper.toResponse(menuItem);
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
    public MenuItemDTOResponse updateMenuItem(Long id, MenuItemDTORequest request) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + id));

        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setCategory(request.category());
        menuItem.setPhotoUrl(request.photoUrl());

        return MenuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Override
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found with id " + id);
        }
        menuItemRepository.deleteById(id);
    }

    @Override
    public MenuItemDTOResponse toggleAvailability(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + id));

        menuItem.setAvailable(!menuItem.getAvailable());

        return MenuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }
}
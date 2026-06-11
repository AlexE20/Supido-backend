package com.backend.supido.menuItem.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.menuItem.mapper.MenuItemMapper;
import com.backend.supido.menuItem.domain.dto.request.MenuItemDTORequest;
import com.backend.supido.menuItem.domain.dto.response.MenuItemDTOResponse;
import com.backend.supido.menuItem.domain.entity.MenuItem;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.menuItem.repository.MenuItemRepository;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        if (menuItemRepository.existsByNameAndRestaurantId(request.name(), restaurantId)) {
            throw new IllegalArgumentException("Menu item with name '" + request.name() + "' already exists in this restaurant");
        }

        MenuItem menuItem = MenuItemMapper.toEntity(request);
        menuItem.setRestaurant(restaurant);

        return MenuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Override
    public MenuItemDTOResponse findMenuItemById(Long restaurantId, Long id) {
        return MenuItemMapper.toResponse(findMenuItemBelongingToRestaurant(restaurantId, id));
    }


    @Override
    public PageableResponse<MenuItemDTOResponse> findAllByRestaurant(Long restaurantId, int page, int size, String sortBy, String sortOrder) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id " + restaurantId);
        }

        Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MenuItemDTOResponse> menuItemPage = menuItemRepository.findByRestaurantId(restaurantId, pageable)
                .map(MenuItemMapper::toResponse);

        if (menuItemPage.getTotalElements() == 0)
            throw new ResourceNotFoundException("No menu items found for restaurant " + restaurantId);

        return PageableResponse.<MenuItemDTOResponse>builder()
                .content(menuItemPage.getContent())
                .page(menuItemPage.getNumber())
                .size(menuItemPage.getSize())
                .totalElements(menuItemPage.getTotalElements())
                .totalPages(menuItemPage.getTotalPages())
                .last(menuItemPage.isLast())
                .build();
    }

    @Override
    public MenuItemDTOResponse updateMenuItem(Long restaurantId, Long id, MenuItemDTORequest request) {
        MenuItem menuItem = findMenuItemBelongingToRestaurant(restaurantId, id);

        if (menuItemRepository.existsByNameAndRestaurantIdAndIdNot(request.name(), restaurantId, id)) {
            throw new IllegalArgumentException("Menu item with name '" + request.name() + "' already exists in this restaurant");
        }

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
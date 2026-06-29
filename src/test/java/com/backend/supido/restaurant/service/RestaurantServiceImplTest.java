package com.backend.supido.restaurant.service;

import com.backend.supido.auth.domain.entity.Role;
import com.backend.supido.common.PageableResponse;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.restaurant.common.enums.Category;
import com.backend.supido.restaurant.domain.dto.request.RestaurantDTORequest;
import com.backend.supido.restaurant.domain.dto.response.RestaurantDTOResponse;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import com.backend.supido.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestaurantServiceImpl Unit Tests")
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private User owner;
    private Restaurant restaurant;
    private RestaurantDTORequest request;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().id(1L).name("ROLE_RESTAURANT").build();

        owner = User.builder()
                .id(10L)
                .username("restaurantOwner")
                .role(role)
                .build();

        restaurant = Restaurant.builder()
                .id(1L)
                .name("La Trattoria")
                .category(Category.ITALIAN)
                .address("Calle Falsa 123")
                .latitude(-34.60)
                .longitude(-58.38)
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(23, 0))
                .photoUrl("https://example.com/photo.jpg")
                .averageRating(4.5)
                .user(owner)
                .build();

        request = RestaurantDTORequest.builder()
                .name("La Trattoria")
                .category(Category.ITALIAN)
                .address("Calle Falsa 123")
                .latitude(-34.60)
                .longitude(-58.38)
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(23, 0))
                .photoUrl("https://example.com/photo.jpg")
                .build();
    }

    // ─── createRestaurant ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("createRestaurant")
    class CreateRestaurant {

        @Test
        @DisplayName("creates and returns response when data is valid")
        void shouldCreateRestaurant_whenDataIsValid() {
            given(restaurantRepository.existsByName("La Trattoria")).willReturn(false);
            given(restaurantRepository.existsByUserId(10L)).willReturn(false);
            given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant);

            RestaurantDTOResponse result = restaurantService.createRestaurant(request, owner);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("La Trattoria");
            assertThat(result.userId()).isEqualTo(10L);
            verify(restaurantRepository).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("throws IllegalArgumentException when restaurant name already exists")
        void shouldThrow_whenNameAlreadyExists() {
            given(restaurantRepository.existsByName("La Trattoria")).willReturn(true);

            assertThatThrownBy(() -> restaurantService.createRestaurant(request, owner))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("La Trattoria");

            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when user already owns a restaurant")
        void shouldThrow_whenUserAlreadyOwnsRestaurant() {
            given(restaurantRepository.existsByName("La Trattoria")).willReturn(false);
            given(restaurantRepository.existsByUserId(10L)).willReturn(true);

            assertThatThrownBy(() -> restaurantService.createRestaurant(request, owner))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already owns");

            verify(restaurantRepository, never()).save(any());
        }
    }

    // ─── findRestaurantById ───────────────────────────────────────────────────

    @Nested
    @DisplayName("findRestaurantById")
    class FindRestaurantById {

        @Test
        @DisplayName("returns response when restaurant exists")
        void shouldReturnResponse_whenRestaurantExists() {
            given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

            RestaurantDTOResponse result = restaurantService.findRestaurantById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("La Trattoria");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when restaurant does not exist")
        void shouldThrow_whenRestaurantNotFound() {
            given(restaurantRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> restaurantService.findRestaurantById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ─── findAllRestaurants ───────────────────────────────────────────────────

    @Nested
    @DisplayName("findAllRestaurants")
    class FindAllRestaurants {

        @Test
        @DisplayName("returns pageable response when restaurants exist")
        void shouldReturnPageableResponse_whenRestaurantsExist() {
            Page<Restaurant> page = new PageImpl<>(List.of(restaurant));
            given(restaurantRepository.findAll(any(Pageable.class))).willReturn(page);

            PageableResponse<RestaurantDTOResponse> result =
                    restaurantService.findAllRestaurants(0, 10, "name", "asc");

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when no restaurants exist")
        void shouldThrow_whenNoRestaurantsFound() {
            Page<Restaurant> emptyPage = new PageImpl<>(Collections.emptyList());
            given(restaurantRepository.findAll(any(Pageable.class))).willReturn(emptyPage);

            assertThatThrownBy(() -> restaurantService.findAllRestaurants(0, 10, "name", "asc"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No restaurants found");
        }

        @Test
        @DisplayName("applies descending sort when sortOrder is desc")
        void shouldApplyDescendingSort_whenSortOrderIsDesc() {
            Page<Restaurant> page = new PageImpl<>(List.of(restaurant));
            given(restaurantRepository.findAll(any(Pageable.class))).willReturn(page);

            PageableResponse<RestaurantDTOResponse> result =
                    restaurantService.findAllRestaurants(0, 5, "name", "desc");

            assertThat(result.getContent()).isNotEmpty();
        }
    }

    // ─── updateRestaurant ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateRestaurant")
    class UpdateRestaurant {

        @Test
        @DisplayName("updates and returns response when data is valid")
        void shouldUpdateRestaurant_whenDataIsValid() {
            given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
            given(restaurantRepository.existsByNameAndIdNot("La Trattoria", 1L)).willReturn(false);
            given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant);

            RestaurantDTOResponse result = restaurantService.updateRestaurant(1L, request, owner);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("La Trattoria");
            verify(restaurantRepository).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when restaurant does not exist")
        void shouldThrow_whenRestaurantNotFound() {
            given(restaurantRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> restaurantService.updateRestaurant(99L, request, owner))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when new name belongs to another restaurant")
        void shouldThrow_whenNameConflictWithAnotherRestaurant() {
            given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
            given(restaurantRepository.existsByNameAndIdNot("La Trattoria", 1L)).willReturn(true);

            assertThatThrownBy(() -> restaurantService.updateRestaurant(1L, request, owner))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("La Trattoria");

            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when requesting user is not the owner")
        void shouldThrow_whenUserIsNotOwner() {
            User anotherUser = User.builder().id(99L).username("intruder").build();

            given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
            given(restaurantRepository.existsByNameAndIdNot("La Trattoria", 1L)).willReturn(false);

            assertThatThrownBy(() -> restaurantService.updateRestaurant(1L, request, anotherUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not allowed");

            verify(restaurantRepository, never()).save(any());
        }
    }

    // ─── deleteRestaurant ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteRestaurant")
    class DeleteRestaurant {

        @Test
        @DisplayName("deletes restaurant when user is the owner")
        void shouldDeleteRestaurant_whenUserIsOwner() {
            given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

            restaurantService.deleteRestaurant(1L, owner);

            verify(restaurantRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when restaurant does not exist")
        void shouldThrow_whenRestaurantNotFound() {
            given(restaurantRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> restaurantService.deleteRestaurant(99L, owner))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(restaurantRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when requesting user is not the owner")
        void shouldThrow_whenUserIsNotOwner() {
            User intruder = User.builder().id(99L).username("intruder").build();
            given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

            assertThatThrownBy(() -> restaurantService.deleteRestaurant(1L, intruder))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not allowed");

            verify(restaurantRepository, never()).deleteById(anyLong());
        }
    }

    // ─── findByCategory ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("findByCategory")
    class FindByCategory {

        @Test
        @DisplayName("returns list when restaurants match the category")
        void shouldReturnList_whenRestaurantsMatchCategory() {
            given(restaurantRepository.findByCategory(Category.ITALIAN)).willReturn(List.of(restaurant));

            List<RestaurantDTOResponse> result = restaurantService.findByCategory("ITALIAN");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).category()).isEqualTo(Category.ITALIAN);
        }

        @Test
        @DisplayName("normalizes category string with spaces to enum")
        void shouldNormalizeCategory_withSpaces() {
            given(restaurantRepository.findByCategory(Category.FAST_FOOD)).willReturn(List.of(restaurant));

            // "fast food" -> "FAST_FOOD" after normalization
            Restaurant fastFood = Restaurant.builder()
                    .id(2L).name("Burger Place").category(Category.FAST_FOOD)
                    .address("Av. Central 1").latitude(-34.0).longitude(-58.0)
                    .openingTime(LocalTime.of(9, 0)).closingTime(LocalTime.of(22, 0))
                    .averageRating(3.8).user(owner).build();

            given(restaurantRepository.findByCategory(Category.FAST_FOOD)).willReturn(List.of(fastFood));

            List<RestaurantDTOResponse> result = restaurantService.findByCategory("fast food");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when category string is invalid")
        void shouldThrow_whenCategoryIsInvalid() {
            assertThatThrownBy(() -> restaurantService.findByCategory("INVALID_CATEGORY"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid category");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when no restaurants match category")
        void shouldThrow_whenNoRestaurantsMatchCategory() {
            given(restaurantRepository.findByCategory(Category.SEAFOOD)).willReturn(Collections.emptyList());

            assertThatThrownBy(() -> restaurantService.findByCategory("SEAFOOD"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("SEAFOOD");
        }
    }

    // ─── findByName ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findByName")
    class FindByName {

        @Test
        @DisplayName("returns list when restaurants match the name")
        void shouldReturnList_whenRestaurantsMatchName() {
            given(restaurantRepository.findByNameContainingIgnoreCase("Trattoria"))
                    .willReturn(List.of(restaurant));

            List<RestaurantDTOResponse> result = restaurantService.findByName("Trattoria");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("La Trattoria");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when no restaurants match name")
        void shouldThrow_whenNoRestaurantsMatchName() {
            given(restaurantRepository.findByNameContainingIgnoreCase(anyString()))
                    .willReturn(Collections.emptyList());

            assertThatThrownBy(() -> restaurantService.findByName("Unknown"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Unknown");
        }
    }

    // ─── getCategories ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getCategories")
    class GetCategories {

        @Test
        @DisplayName("returns all available categories")
        void shouldReturnAllCategories() {
            Category[] result = restaurantService.getCategories();

            assertThat(result).isNotEmpty();
            assertThat(result).containsExactlyInAnyOrder(Category.values());
        }
    }
}
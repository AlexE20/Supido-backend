package com.backend.supido.rating.service;

import com.backend.supido.auth.domain.entity.Role;
import com.backend.supido.common.PageableResponse;
import com.backend.supido.deliveryPerson.domain.entity.DeliveryPerson;
import com.backend.supido.deliveryPerson.repository.DeliveryPersonRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.order.common.enums.Status;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.rating.common.enums.RatingType;
import com.backend.supido.rating.domain.dto.request.RatingDTORequest;
import com.backend.supido.rating.domain.dto.response.RatingDTOResponse;
import com.backend.supido.rating.domain.entity.Rating;
import com.backend.supido.rating.repository.RatingRepository;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import com.backend.supido.restaurant.common.enums.Category;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RatingServiceImpl Unit Tests")
class RatingServiceImplTest {

    @Mock private RatingRepository ratingRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private DeliveryPersonRepository deliveryPersonRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private User customer;
    private Restaurant restaurant;
    private DeliveryPerson deliveryPerson;
    private Order deliveredOrder;
    private Order pendingOrder;
    private Order orderWithoutDriver;
    private Rating savedRating;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().id(1L).name("ROLE_USER").build();

        customer = User.builder().id(5L).username("customer").role(role).build();

        restaurant = Restaurant.builder()
                .id(1L).name("La Trattoria").category(Category.ITALIAN)
                .address("Calle 1").latitude(-34.0).longitude(-58.0)
                .openingTime(LocalTime.of(9, 0)).closingTime(LocalTime.of(22, 0))
                .averageRating(4.0).user(customer).build();

        deliveryPerson = DeliveryPerson.builder()
                .id(2L).userId(20L).available(true).averageRating(3.5).build();

        deliveredOrder = Order.builder()
                .id(100L).status(Status.DELIVERED)
                .restaurant(restaurant).deliveryPerson(deliveryPerson).user(customer).build();

        pendingOrder = Order.builder()
                .id(101L).status(Status.PENDING)
                .restaurant(restaurant).deliveryPerson(deliveryPerson).user(customer).build();

        orderWithoutDriver = Order.builder()
                .id(102L).status(Status.DELIVERED)
                .restaurant(restaurant).deliveryPerson(null).user(customer).build();

        savedRating = Rating.builder()
                .id(1L).ratedById(5L).type(RatingType.RESTAURANT)
                .score(5).createdAt(LocalDateTime.now()).order(deliveredOrder).build();
    }

    // ─── createRating – RESTAURANT type ──────────────────────────────────────

    @Nested
    @DisplayName("createRating – RESTAURANT")
    class CreateRatingRestaurant {

        private RatingDTORequest restaurantRequest;

        @BeforeEach
        void setup() {
            restaurantRequest = RatingDTORequest.builder()
                    .orderId(100L).ratedById(5L).type(RatingType.RESTAURANT).score(5).build();
        }

        @Test
        @DisplayName("creates rating and updates restaurant average")
        void shouldCreateRating_andUpdateRestaurantAverage() {
            given(orderRepository.findById(100L)).willReturn(Optional.of(deliveredOrder));
            given(ratingRepository.existsByOrder_IdAndType(100L, RatingType.RESTAURANT)).willReturn(false);
            given(ratingRepository.save(any(Rating.class))).willAnswer(inv -> {
                Rating r = inv.getArgument(0);
                r.setId(1L);
                return r;
            });
            given(ratingRepository.calculateAverageByRestaurantId(1L)).willReturn(4.8);
            given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant);
            given(ratingRepository.findById(1L)).willReturn(Optional.of(savedRating));

            RatingDTOResponse result = ratingService.createRating(restaurantRequest);

            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo(RatingType.RESTAURANT);
            assertThat(result.score()).isEqualTo(5);
            verify(restaurantRepository).save(restaurant);
        }

        @Test
        @DisplayName("uses 0.0 average when no prior ratings exist for restaurant")
        void shouldUseZeroAverage_whenNoRestaurantRatings() {
            given(orderRepository.findById(100L)).willReturn(Optional.of(deliveredOrder));
            given(ratingRepository.existsByOrder_IdAndType(100L, RatingType.RESTAURANT)).willReturn(false);
            given(ratingRepository.save(any(Rating.class))).willAnswer(inv -> {
                Rating r = inv.getArgument(0);
                r.setId(1L);
                return r;
            });
            given(ratingRepository.calculateAverageByRestaurantId(1L)).willReturn(null);
            given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant);
            given(ratingRepository.findById(1L)).willReturn(Optional.of(savedRating));

            ratingService.createRating(restaurantRequest);

            verify(restaurantRepository).save(any(Restaurant.class));
            assertThat(restaurant.getAverageRating()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when order does not exist")
        void shouldThrow_whenOrderNotFound() {
            given(orderRepository.findById(100L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> ratingService.createRating(restaurantRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("100");

            verify(ratingRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when order is not DELIVERED")
        void shouldThrow_whenOrderNotDelivered() {
            given(orderRepository.findById(100L)).willReturn(Optional.of(pendingOrder));

            assertThatThrownBy(() -> ratingService.createRating(restaurantRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("DELIVERED");

            verify(ratingRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when order was already rated for RESTAURANT")
        void shouldThrow_whenAlreadyRatedForRestaurant() {
            given(orderRepository.findById(100L)).willReturn(Optional.of(deliveredOrder));
            given(ratingRepository.existsByOrder_IdAndType(100L, RatingType.RESTAURANT)).willReturn(true);

            assertThatThrownBy(() -> ratingService.createRating(restaurantRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already been rated");

            verify(ratingRepository, never()).save(any());
        }
    }

    // ─── createRating – DELIVERY_PERSON type ─────────────────────────────────

    @Nested
    @DisplayName("createRating – DELIVERY_PERSON")
    class CreateRatingDeliveryPerson {

        private RatingDTORequest driverRequest;

        @BeforeEach
        void setup() {
            driverRequest = RatingDTORequest.builder()
                    .orderId(100L).ratedById(5L).type(RatingType.DELIVERY_PERSON).score(4).build();
        }

        @Test
        @DisplayName("creates rating and updates delivery person average")
        void shouldCreateRating_andUpdateDeliveryPersonAverage() {
            Rating driverRating = Rating.builder()
                    .id(2L).ratedById(5L).type(RatingType.DELIVERY_PERSON)
                    .score(4).createdAt(LocalDateTime.now()).order(deliveredOrder).build();

            given(orderRepository.findById(100L)).willReturn(Optional.of(deliveredOrder));
            given(ratingRepository.existsByOrder_IdAndType(100L, RatingType.DELIVERY_PERSON)).willReturn(false);
            given(ratingRepository.save(any(Rating.class))).willAnswer(inv -> {
                Rating r = inv.getArgument(0);
                r.setId(2L);
                return r;
            });
            given(ratingRepository.calculateAverageByDeliveryPersonId(2L)).willReturn(4.2);
            given(deliveryPersonRepository.save(any(DeliveryPerson.class))).willReturn(deliveryPerson);
            given(ratingRepository.findById(2L)).willReturn(Optional.of(driverRating));

            RatingDTOResponse result = ratingService.createRating(driverRequest);

            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo(RatingType.DELIVERY_PERSON);
            verify(deliveryPersonRepository).save(deliveryPerson);
            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("uses 0.0 average when no prior ratings exist for delivery person")
        void shouldUseZeroAverage_whenNoDeliveryPersonRatings() {
            Rating driverRating = Rating.builder()
                    .id(2L).ratedById(5L).type(RatingType.DELIVERY_PERSON)
                    .score(4).createdAt(LocalDateTime.now()).order(deliveredOrder).build();

            given(orderRepository.findById(100L)).willReturn(Optional.of(deliveredOrder));
            given(ratingRepository.existsByOrder_IdAndType(100L, RatingType.DELIVERY_PERSON)).willReturn(false);
            given(ratingRepository.save(any(Rating.class))).willAnswer(inv -> {
                Rating r = inv.getArgument(0);
                r.setId(2L);
                return r;
            });
            given(ratingRepository.calculateAverageByDeliveryPersonId(2L)).willReturn(null);
            given(deliveryPersonRepository.save(any(DeliveryPerson.class))).willReturn(deliveryPerson);
            given(ratingRepository.findById(2L)).willReturn(Optional.of(driverRating));

            ratingService.createRating(driverRequest);

            assertThat(deliveryPerson.getAverageRating()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when order has no delivery person assigned")
        void shouldThrow_whenOrderHasNoDeliveryPerson() {
            given(orderRepository.findById(102L)).willReturn(Optional.of(orderWithoutDriver));
            given(ratingRepository.existsByOrder_IdAndType(102L, RatingType.DELIVERY_PERSON)).willReturn(false);
            RatingDTORequest requestForNoDriver = RatingDTORequest.builder()
                    .orderId(102L).ratedById(5L).type(RatingType.DELIVERY_PERSON).score(4).build();

            assertThatThrownBy(() -> ratingService.createRating(requestForNoDriver))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("delivery person");

            verify(ratingRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when order was already rated for DELIVERY_PERSON")
        void shouldThrow_whenAlreadyRatedForDeliveryPerson() {
            given(orderRepository.findById(100L)).willReturn(Optional.of(deliveredOrder));
            given(ratingRepository.existsByOrder_IdAndType(100L, RatingType.DELIVERY_PERSON)).willReturn(true);

            assertThatThrownBy(() -> ratingService.createRating(driverRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already been rated");

            verify(ratingRepository, never()).save(any());
        }
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns response when rating exists")
        void shouldReturnResponse_whenRatingExists() {
            given(ratingRepository.findById(1L)).willReturn(Optional.of(savedRating));

            RatingDTOResponse result = ratingService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.score()).isEqualTo(5);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when rating does not exist")
        void shouldThrow_whenRatingNotFound() {
            given(ratingRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> ratingService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ─── findByRestaurant ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("findByRestaurant")
    class FindByRestaurant {

        @Test
        @DisplayName("returns pageable response when ratings exist for restaurant")
        void shouldReturnPageableResponse_whenRatingsExist() {
            given(restaurantRepository.existsById(1L)).willReturn(true);
            Page<Rating> page = new PageImpl<>(List.of(savedRating));
            given(ratingRepository.findByOrder_Restaurant_Id(any(Long.class), any(Pageable.class)))
                    .willReturn(page);

            PageableResponse<RatingDTOResponse> result =
                    ratingService.findByRestaurant(1L, 0, 10, "createdAt", "desc");

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1L);
        }

        @Test
        @DisplayName("returns empty page when no ratings for restaurant")
        void shouldReturnEmptyPage_whenNoRatings() {
            given(restaurantRepository.existsById(1L)).willReturn(true);
            Page<Rating> emptyPage = new PageImpl<>(Collections.emptyList());
            given(ratingRepository.findByOrder_Restaurant_Id(any(Long.class), any(Pageable.class)))
                    .willReturn(emptyPage);

            PageableResponse<RatingDTOResponse> result =
                    ratingService.findByRestaurant(1L, 0, 10, "createdAt", "asc");

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when restaurant does not exist")
        void shouldThrow_whenRestaurantNotFound() {
            given(restaurantRepository.existsById(99L)).willReturn(false);

            assertThatThrownBy(() -> ratingService.findByRestaurant(99L, 0, 10, "createdAt", "desc"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ─── findByRatedBy ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findByRatedBy")
    class FindByRatedBy {

        @Test
        @DisplayName("returns pageable response with ratings made by a specific user")
        void shouldReturnPageableResponse_whenRatingsExist() {
            Page<Rating> page = new PageImpl<>(List.of(savedRating));
            given(ratingRepository.findByRatedById(any(Long.class), any(Pageable.class)))
                    .willReturn(page);

            PageableResponse<RatingDTOResponse> result =
                    ratingService.findByRatedBy(5L, 0, 10, "createdAt", "desc");

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("returns empty page when user has made no ratings")
        void shouldReturnEmptyPage_whenNoRatings() {
            Page<Rating> emptyPage = new PageImpl<>(Collections.emptyList());
            given(ratingRepository.findByRatedById(any(Long.class), any(Pageable.class)))
                    .willReturn(emptyPage);

            PageableResponse<RatingDTOResponse> result =
                    ratingService.findByRatedBy(5L, 0, 10, "createdAt", "asc");

            assertThat(result.getContent()).isEmpty();
        }
    }
}
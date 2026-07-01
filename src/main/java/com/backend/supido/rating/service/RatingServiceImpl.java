package com.backend.supido.rating.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.deliveryPerson.domain.entity.DeliveryPerson;
import com.backend.supido.deliveryPerson.repository.DeliveryPersonRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.order.common.enums.Status;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.rating.common.mapper.RatingMapper;
import com.backend.supido.rating.domain.dto.request.RatingDTORequest;
import com.backend.supido.rating.domain.dto.response.RatingDTOResponse;
import com.backend.supido.rating.domain.entity.Rating;
import com.backend.supido.rating.common.enums.RatingType;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.rating.repository.RatingRepository;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;

    @Override
    @Transactional
    public RatingDTOResponse createRating(RatingDTORequest request, User user) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id " + request.orderId()));

        if (order.getStatus() != Status.DELIVERED) {
            throw new IllegalArgumentException("Order must be DELIVERED before it can be rated");
        }

        if (ratingRepository.existsByOrder_IdAndType(order.getId(), request.type())) {
            throw new IllegalArgumentException(
                    "This order has already been rated for type " + request.type());
        }

        if (request.type() == RatingType.DELIVERY_PERSON && order.getDeliveryPerson() == null) {
            throw new IllegalArgumentException("Order has no assigned delivery person to rate");
        }

        Rating rating = RatingMapper.toEntity(request, user.getId());
        rating.setOrder(order);
        ratingRepository.save(rating);

        if (request.type() == RatingType.RESTAURANT) {
            Double avg = ratingRepository.calculateAverageByRestaurantId(order.getRestaurant().getId());
            Restaurant restaurant = order.getRestaurant();
            restaurant.setAverageRating(avg != null ? avg : 0.0);
            restaurantRepository.save(restaurant);
        } else {
            DeliveryPerson deliveryPerson = order.getDeliveryPerson();
            Double avg = ratingRepository.calculateAverageByDeliveryPersonId(deliveryPerson.getId());
            deliveryPerson.setAverageRating(avg != null ? avg : 0.0);
            deliveryPersonRepository.save(deliveryPerson);
        }

        return RatingMapper.toResponse(ratingRepository.findById(rating.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found")));
    }

    @Override
    public List<RatingDTOResponse> findByOrderId(Long orderId) {
        return ratingRepository.findByOrder_Id(orderId)
                .stream()
                .map(RatingMapper::toResponse)
                .toList();
    }

    @Override
    public RatingDTOResponse findById(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rating not found with id " + id));
        return RatingMapper.toResponse(rating);
    }

    @Override
    public PageableResponse<RatingDTOResponse> findByRestaurant(
            Long restaurantId, int page, int size, String sortBy, String sortOrder) {

        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id " + restaurantId);
        }

        Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RatingDTOResponse> ratingPage = ratingRepository
                .findByOrder_Restaurant_Id(restaurantId, pageable)
                .map(RatingMapper::toResponse);

        return PageableResponse.<RatingDTOResponse>builder()
                .content(ratingPage.getContent())
                .page(ratingPage.getNumber())
                .size(ratingPage.getSize())
                .totalElements(ratingPage.getTotalElements())
                .totalPages(ratingPage.getTotalPages())
                .last(ratingPage.isLast())
                .build();
    }

    @Override
    public PageableResponse<RatingDTOResponse> findByRatedBy(
            Long ratedById, int page, int size, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RatingDTOResponse> ratingPage = ratingRepository
                .findByRatedById(ratedById, pageable)
                .map(RatingMapper::toResponse);

        return PageableResponse.<RatingDTOResponse>builder()
                .content(ratingPage.getContent())
                .page(ratingPage.getNumber())
                .size(ratingPage.getSize())
                .totalElements(ratingPage.getTotalElements())
                .totalPages(ratingPage.getTotalPages())
                .last(ratingPage.isLast())
                .build();
    }
}
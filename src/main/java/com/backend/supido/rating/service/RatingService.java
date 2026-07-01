package com.backend.supido.rating.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.rating.domain.dto.request.RatingDTORequest;
import com.backend.supido.rating.domain.dto.response.RatingDTOResponse;
import com.backend.supido.user.domain.entity.User;

import java.util.List;

public interface RatingService {
    RatingDTOResponse createRating(RatingDTORequest request, User user);
    RatingDTOResponse findById(Long id);
    List<RatingDTOResponse> findByOrderId(Long orderId);
    PageableResponse<RatingDTOResponse> findByRestaurant(Long restaurantId, int page, int size, String sortBy, String sortOrder);
    PageableResponse<RatingDTOResponse> findByRatedBy(Long ratedById, int page, int size, String sortBy, String sortOrder);
}
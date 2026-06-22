package com.backend.supido.rating.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.rating.domain.dto.request.RatingDTORequest;
import com.backend.supido.rating.domain.dto.response.RatingDTOResponse;

public interface RatingService {
    RatingDTOResponse createRating(RatingDTORequest request);
    RatingDTOResponse findById(Long id);
    PageableResponse<RatingDTOResponse> findByRestaurant(Long restaurantId, int page, int size, String sortBy, String sortOrder);
    PageableResponse<RatingDTOResponse> findByRatedBy(Long ratedById, int page, int size, String sortBy, String sortOrder);
}
package com.backend.supido.deliveryPerson.service;

import com.backend.supido.deliveryPerson.domain.dto.request.CreateDeliveryPersonRequest;
import com.backend.supido.deliveryPerson.domain.dto.request.UpdateDeliveryPersonRequest;
import com.backend.supido.deliveryPerson.domain.dto.response.DeliveryPersonResponse;

import java.util.List;

public interface DeliveryPersonService {
    DeliveryPersonResponse create(CreateDeliveryPersonRequest request);

    DeliveryPersonResponse findById(Long id);
    DeliveryPersonResponse findByUserId(Long userId);
    List<DeliveryPersonResponse> findAll();
    List<DeliveryPersonResponse> findAvailable();
    List<Long> findNearbyAvailableUserIds(Double targetLat, Double targetLng, double radiusKm);

    DeliveryPersonResponse update(Long id, UpdateDeliveryPersonRequest request);
    void delete(Long id);

    DeliveryPersonResponse updateLocation(Long id, double latitude, double longitude);
}
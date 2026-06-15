package com.backend.supido.deliveryPerson.service;

import com.backend.supido.deliveryPerson.domain.dto.request.CreateDeliveryPersonRequest;
import com.backend.supido.deliveryPerson.domain.dto.request.UpdateDeliveryPersonRequest;
import com.backend.supido.deliveryPerson.domain.dto.response.DeliveryPersonResponse;
import com.backend.supido.deliveryPerson.domain.entity.DeliveryPerson;
import com.backend.supido.deliveryPerson.mapper.DeliveryPersonMapper;
import com.backend.supido.deliveryPerson.repository.DeliveryPersonRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryPersonServiceImpl implements DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;

    @Override
    public DeliveryPersonResponse create(CreateDeliveryPersonRequest request) {
        if (deliveryPersonRepository.findByUserId(request.userId()).isPresent()) {
            throw new IllegalArgumentException("A delivery person already exists for userId: " + request.userId());
        }
        DeliveryPerson saved = deliveryPersonRepository.save(DeliveryPersonMapper.toEntity(request));
        return DeliveryPersonMapper.toDto(saved);
    }

    @Override
    public DeliveryPersonResponse findById(Long id) {
        return DeliveryPersonMapper.toDto(findOrThrow(id));
    }

    @Override
    public List<DeliveryPersonResponse> findAll() {
        return deliveryPersonRepository.findAll()
                .stream()
                .map(DeliveryPersonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryPersonResponse> findAvailable() {
        return deliveryPersonRepository.findByAvailableTrue()
                .stream()
                .map(DeliveryPersonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DeliveryPersonResponse update(Long id, UpdateDeliveryPersonRequest request) {
        DeliveryPerson existing = findOrThrow(id);
        if (request.available() != null) existing.setAvailable(request.available());
        if (request.latitude() != null) {
            existing.setLatitude(request.latitude());
            existing.setLastLocationAt(LocalDateTime.now());
        }
        if (request.longitude() != null) existing.setLongitude(request.longitude());
        return DeliveryPersonMapper.toDto(deliveryPersonRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        deliveryPersonRepository.deleteById(id);
    }

    @Override
    public DeliveryPersonResponse updateLocation(Long id, double latitude, double longitude) {
        DeliveryPerson existing = findOrThrow(id);
        existing.setLatitude(latitude);
        existing.setLongitude(longitude);
        existing.setLastLocationAt(LocalDateTime.now());
        return DeliveryPersonMapper.toDto(deliveryPersonRepository.save(existing));
    }

    private DeliveryPerson findOrThrow(Long id) {
        return deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryPerson not found with id: " + id));
    }
}
package com.backend.supido.deliveryPerson.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.deliveryPerson.domain.dto.request.CreateDeliveryPersonRequest;
import com.backend.supido.deliveryPerson.domain.dto.request.UpdateDeliveryPersonRequest;
import com.backend.supido.deliveryPerson.service.DeliveryPersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/delivery-persons")
@RequiredArgsConstructor
public class DeliveryPersonController {

    private final DeliveryPersonService deliveryPersonService;

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateDeliveryPersonRequest request) {
        return buildResponse("Delivery person created successfully", HttpStatus.CREATED, deliveryPersonService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Delivery person retrieved successfully", HttpStatus.OK, deliveryPersonService.findById(id));
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll() {
        return buildResponse("Delivery persons retrieved successfully", HttpStatus.OK, deliveryPersonService.findAll());
    }

    @GetMapping("/available")
    public ResponseEntity<GeneralResponse> findAvailable() {
        return buildResponse("Available delivery persons retrieved successfully", HttpStatus.OK, deliveryPersonService.findAvailable());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateDeliveryPersonRequest request) {
        return buildResponse("Delivery person updated successfully", HttpStatus.OK, deliveryPersonService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long id) {
        deliveryPersonService.delete(id);
        return buildResponse("Delivery person deleted successfully", HttpStatus.OK, null);
    }

    private ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build());
    }
}
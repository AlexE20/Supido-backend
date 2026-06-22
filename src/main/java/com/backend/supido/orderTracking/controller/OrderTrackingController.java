package com.backend.supido.orderTracking.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.orderTracking.domain.dto.request.CreateOrderTrackingRequest;
import com.backend.supido.orderTracking.domain.dto.request.UpdateOrderTrackingRequest;
import com.backend.supido.orderTracking.service.OrderTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/order-tracking")
@RequiredArgsConstructor
public class OrderTrackingController {

    private final OrderTrackingService orderTrackingService;

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateOrderTrackingRequest request) {
        return buildResponse("Order tracking created successfully", HttpStatus.CREATED, orderTrackingService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Order tracking retrieved successfully", HttpStatus.OK, orderTrackingService.findById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> findByOrderId(@PathVariable Long orderId) {
        return buildResponse("Order tracking retrieved successfully", HttpStatus.OK, orderTrackingService.findByOrderId(orderId));
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll() {
        return buildResponse("Order trackings retrieved successfully", HttpStatus.OK, orderTrackingService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateOrderTrackingRequest request) {
        return buildResponse("Order tracking updated successfully", HttpStatus.OK, orderTrackingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long id) {
        orderTrackingService.delete(id);
        return buildResponse("Order tracking deleted successfully", HttpStatus.OK, null);
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
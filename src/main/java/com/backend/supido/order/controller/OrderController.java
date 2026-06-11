package com.backend.supido.order.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.service.OrderService;
import com.backend.supido.order.service.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return buildResponse("Order created successfully", HttpStatus.CREATED, orderService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Order retrieved successfully", HttpStatus.OK, orderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll() {
        return buildResponse("Orders retrieved successfully", HttpStatus.OK, orderService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateOrderRequest request) {
        return buildResponse("Order updated successfully", HttpStatus.OK, orderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return buildResponse("Order cancelled successfully", HttpStatus.OK, null);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<GeneralResponse> confirm(@PathVariable Long id) {
        return buildResponse("Order confirmed successfully", HttpStatus.OK, orderService.confirm(id));
    }

    @PatchMapping("/{id}/prepare")
    public ResponseEntity<GeneralResponse> prepare(@PathVariable Long id) {
        return buildResponse("Order preparing", HttpStatus.OK, orderService.prepare(id));
    }

    @PatchMapping("/{id}/on-the-way")
    public ResponseEntity<GeneralResponse> onTheWay(@PathVariable Long id) {
        return buildResponse("Order on the way", HttpStatus.OK, orderService.onTheWay(id));
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<GeneralResponse> deliver(@PathVariable Long id) {
        return buildResponse("Order delivered successfully", HttpStatus.OK, orderService.deliver(id));
    }

    @PatchMapping("/{id}/assign-delivery-person")
    public ResponseEntity<GeneralResponse> assignDeliveryPerson(@PathVariable Long id, @RequestParam Long deliveryPersonId) {
        return buildResponse("Delivery person assigned successfully", HttpStatus.OK, orderService.assignDeliveryPerson(id, deliveryPersonId));
    }

    // Consultas por relación
    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse> findByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return buildResponse("Orders retrieved successfully", HttpStatus.OK, orderService.findByUserId(userId, page, size));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<GeneralResponse> findByRestaurantId(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return buildResponse("Orders retrieved successfully", HttpStatus.OK, orderService.findByRestaurantId(restaurantId, page, size));
    }

    @GetMapping("/delivery-person/{deliveryPersonId}")
    public ResponseEntity<GeneralResponse> findByDeliveryPersonId(
            @PathVariable Long deliveryPersonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return buildResponse("Orders retrieved successfully", HttpStatus.OK, orderService.findByDeliveryPersonId(deliveryPersonId, page, size));
    }

    public ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data){
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build()
                );
    }
}

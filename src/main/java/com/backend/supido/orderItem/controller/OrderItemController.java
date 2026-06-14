package com.backend.supido.orderItem.controller;


import com.backend.supido.common.GeneralResponse;
import com.backend.supido.orderItem.domain.dto.request.CreateOrderItemRequest;
import com.backend.supido.orderItem.domain.dto.request.UpdateOrderItemRequest;
import com.backend.supido.orderItem.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<GeneralResponse> findAllByOrderId(@PathVariable Long orderId) {
        return buildResponse("Order items retrieved successfully", HttpStatus.OK,
                orderItemService.findAllByOrderId(orderId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long orderId,
                                                    @PathVariable Long itemId) {
        return buildResponse("Order item retrieved successfully", HttpStatus.OK,
                orderItemService.findById(orderId, itemId));
    }

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@PathVariable Long orderId,
                                                  @Valid @RequestBody CreateOrderItemRequest request) {
        return buildResponse("Order item created successfully", HttpStatus.CREATED,
                orderItemService.create(orderId, request));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long orderId,
                                                  @PathVariable Long itemId,
                                                  @Valid @RequestBody UpdateOrderItemRequest request) {
        return buildResponse("Order item updated successfully", HttpStatus.OK,
                orderItemService.update(orderId, itemId, request));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long orderId,
                                                  @PathVariable Long itemId) {
        orderItemService.delete(orderId, itemId);
        return buildResponse("Order item deleted successfully", HttpStatus.OK, null);
    }

    private ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
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

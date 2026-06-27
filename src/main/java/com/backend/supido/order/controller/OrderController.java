package com.backend.supido.order.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.service.OrderService;
import com.backend.supido.user.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping //Al crear no se estan
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateOrderRequest request,
                                                  @AuthenticationPrincipal User user) {
        return buildResponse("Order created successfully", HttpStatus.CREATED, orderService.create(request, user));
    }
    @PreAuthorize("hasRole('SUPER')")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Order retrieved successfully", HttpStatus.OK, orderService.findById(id));
    }

    @PreAuthorize("hasRole('SUPER')")
    @GetMapping
    public ResponseEntity<GeneralResponse> findAll() {
        return buildResponse("Orders retrieved successfully", HttpStatus.OK, orderService.findAll());
    }

    @PutMapping("/{id}") //Agregar userID
    public ResponseEntity<GeneralResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateOrderRequest request,
                                                  @AuthenticationPrincipal User user) {
        return buildResponse("Order updated successfully", HttpStatus.OK, orderService.update(id, request, user));
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<GeneralResponse> getReceipt(@PathVariable Long id,@AuthenticationPrincipal User user) {
        return buildResponse("Receipt retrieved successfully", HttpStatus.OK, orderService.getReceipt(id,user));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<GeneralResponse> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return buildResponse("Order cancelled successfully", HttpStatus.OK, null);
    }

    @PreAuthorize("hasRole('RESTAURANT') or hasRole('SUPER')")
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<GeneralResponse> confirm(@PathVariable Long id) {
        return buildResponse("Order confirmed successfully", HttpStatus.OK, orderService.confirm(id));
    }

    @PreAuthorize("hasRole('RESTAURANT') or hasRole('SUPER')")
    @PatchMapping("/{id}/prepare")
    public ResponseEntity<GeneralResponse> prepare(@PathVariable Long id) {
        return buildResponse("Order preparing", HttpStatus.OK, orderService.prepare(id));
    }

    @PreAuthorize("hasRole('RESTAURANT') or hasRole('SUPER')")
    @PatchMapping("/{id}/on-the-way")
    public ResponseEntity<GeneralResponse> onTheWay(@PathVariable Long id) {
        return buildResponse("Order on the way", HttpStatus.OK, orderService.onTheWay(id));
    }

    @PreAuthorize("hasRole('RESTAURANT') or hasRole('SUPER')")
    @PatchMapping("/{id}/deliver")
    public ResponseEntity<GeneralResponse> deliver(@PathVariable Long id) {
        return buildResponse("Order delivered successfully", HttpStatus.OK, orderService.deliver(id));
    }

    @PreAuthorize("hasRole('RESTAURANT') or hasRole('SUPER')")
    @PatchMapping("/{id}/assign-delivery-person")
    public ResponseEntity<GeneralResponse> assignDeliveryPerson(@PathVariable Long id, @RequestParam Long deliveryPersonId) {
        return buildResponse("Delivery person assigned successfully", HttpStatus.OK, orderService.assignDeliveryPerson(id, deliveryPersonId));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<GeneralResponse> acceptOrder(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return buildResponse("Order accepted successfully", HttpStatus.OK, orderService.acceptOrder(id, user));
    }

    @PreAuthorize("hasRole('DELIVERY') or hasRole('SUPER')")
    @PatchMapping("/{id}/confirm-cash-payment")
    public ResponseEntity<GeneralResponse> confirmCashPayment(@PathVariable Long id, @RequestParam Long deliveryPersonId) {
        orderService.confirmCashPayment(id, deliveryPersonId);
        return buildResponse("Cash payment successfully confirmed", HttpStatus.OK, null);
    }

    @GetMapping("/{id}/order-stats")
    public ResponseEntity<GeneralResponse> getOrderStats(@PathVariable Long id) {
        return buildResponse("Order stats retrieved successfully", HttpStatus.OK, orderService.getOrderStats(id));
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
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        return buildResponse("Orders retrieved successfully", HttpStatus.OK, orderService.findByDeliveryPersonId(deliveryPersonId, page, size, user));
    }

    @GetMapping("/delivery-person/{deliveryPersonId}/delivered")
    public ResponseEntity<GeneralResponse> findDeliveredByDeliveryPersonId(
            @PathVariable Long deliveryPersonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        return buildResponse("Delivered orders retrieved successfully", HttpStatus.OK,
                orderService.findDeliveredOrdersByDeliveryPersonId(deliveryPersonId, page, size, user));
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

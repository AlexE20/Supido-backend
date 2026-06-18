package com.backend.supido.payment.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Payment retrieved", HttpStatus.OK, paymentService.findById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> findByOrderId(@PathVariable Long orderId) {
        return buildResponse("Payment retrieved", HttpStatus.OK, paymentService.findByOrderId(orderId));
    }

    public ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
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

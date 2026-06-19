package com.backend.supido.claim.controller;

import com.backend.supido.claim.domain.dto.request.ApproveClaimRequest;
import com.backend.supido.claim.domain.dto.request.CreateClaimRequest;
import com.backend.supido.claim.service.ClaimService;
import com.backend.supido.common.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateClaimRequest request) {
        return buildResponse("Claim created", HttpStatus.CREATED, claimService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Claim retrieved", HttpStatus.OK, claimService.findById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> findByOrderId(@PathVariable Long orderId) {
        return buildResponse("Claims retrieved", HttpStatus.OK, claimService.findByOrderId(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse> findByUserId(@PathVariable Long userId) {
        return buildResponse("Claims retrieved", HttpStatus.OK, claimService.findByUserId(userId));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<GeneralResponse> approve(@PathVariable Long id, @Valid @RequestBody ApproveClaimRequest request) {
        return buildResponse("Claim approved", HttpStatus.OK, claimService.approve(id, request));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<GeneralResponse> reject(@PathVariable Long id) {
        return buildResponse("Claim rejected", HttpStatus.OK, claimService.reject(id));
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

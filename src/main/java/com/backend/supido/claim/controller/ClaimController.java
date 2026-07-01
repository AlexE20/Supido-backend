package com.backend.supido.claim.controller;

import com.backend.supido.claim.domain.dto.request.ApproveClaimRequest;
import com.backend.supido.claim.domain.dto.request.CreateClaimRequest;
import com.backend.supido.claim.service.ClaimService;
import com.backend.supido.common.GeneralResponse;
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
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateClaimRequest request, @AuthenticationPrincipal User user) {
        return buildResponse("Claim created", HttpStatus.CREATED, claimService.create(request, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return buildResponse("Claim retrieved", HttpStatus.OK, claimService.findById(id, user));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> findByOrderId(@PathVariable Long orderId, @AuthenticationPrincipal User user) {
        return buildResponse("Claims retrieved", HttpStatus.OK, claimService.findByOrderId(orderId, user));
    }

    @GetMapping("/my-claims")
    public ResponseEntity<GeneralResponse> findMyClaims(@AuthenticationPrincipal User user) {
        return buildResponse("Claims retrieved", HttpStatus.OK, claimService.findByUserId(user.getId()));
    }

    @PreAuthorize("hasRole('RESTAURANT') or hasRole('SUPER')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<GeneralResponse> approve(@PathVariable Long id, @Valid @RequestBody ApproveClaimRequest request) {
        return buildResponse("Claim approved", HttpStatus.OK, claimService.approve(id, request));
    }

    @PreAuthorize("hasRole('RESTAURANT') or hasRole('SUPER')")
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

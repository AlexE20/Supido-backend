package com.backend.supido.userAddress.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.userAddress.domain.dto.request.UserAddressRequest;
import com.backend.supido.userAddress.service.UserAddressServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/users/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressServiceImpl userAddressService;

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody UserAddressRequest request,
                                                  @AuthenticationPrincipal User user) {
        return buildResponse("Address created successfully", HttpStatus.CREATED, userAddressService.create(request, user));
    }


    @GetMapping("/name/{addressName}")







    public ResponseEntity<GeneralResponse> findByName(@PathVariable String addressName,
                                                      @AuthenticationPrincipal User user) {
        return buildResponse("Address retrieved successfully", HttpStatus.OK, userAddressService.findByName(addressName, user));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long addressId,
                                                    @AuthenticationPrincipal User user) {
        return buildResponse("Address retrieved successfully", HttpStatus.OK, userAddressService.findById(addressId, user));
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll(@AuthenticationPrincipal User user) {
        return buildResponse("Addresses retrieved successfully", HttpStatus.OK, userAddressService.findAllByUser(user));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long addressId,
                                                  @Valid @RequestBody UserAddressRequest request,
                                                  @AuthenticationPrincipal User user) {
        return buildResponse("Address updated successfully", HttpStatus.OK, userAddressService.update(addressId, request, user));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long addressId,
                                                  @AuthenticationPrincipal User user) {
        userAddressService.delete( addressId, user);
        return buildResponse("Address deleted successfully", HttpStatus.OK, null);
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
                        .build()
                );
    }
}

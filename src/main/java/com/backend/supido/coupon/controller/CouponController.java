package com.backend.supido.coupon.controller;


import com.backend.supido.common.GeneralResponse;
import com.backend.supido.coupon.domain.dto.request.CreateCouponRequest;
import com.backend.supido.coupon.domain.dto.request.UpdateCouponRequest;
import com.backend.supido.coupon.service.CouponServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponServiceImpl couponService;

    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        return buildResponse("Coupon created successfully", HttpStatus.CREATED, couponService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> findById(@PathVariable Long id) {
        return buildResponse("Coupon retrieved successfully", HttpStatus.OK, couponService.findById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<GeneralResponse> findByCode(@PathVariable String code) {
        return buildResponse("Coupon retrieved successfully", HttpStatus.OK, couponService.findByCode(code));
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return buildResponse("Coupons retrieved successfully", HttpStatus.OK, couponService.findAll(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateCouponRequest request) {
        return buildResponse("Coupon updated successfully", HttpStatus.OK, couponService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> delete(@PathVariable Long id) {
        couponService.delete(id);
        return buildResponse("Coupon deleted successfully", HttpStatus.OK, null);
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

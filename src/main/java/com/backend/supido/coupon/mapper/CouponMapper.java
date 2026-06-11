package com.backend.supido.coupon.mapper;

import com.backend.supido.coupon.domain.dto.request.CreateCouponRequest;
import com.backend.supido.coupon.domain.dto.request.UpdateCouponRequest;
import com.backend.supido.coupon.domain.dto.response.CouponResponse;
import com.backend.supido.coupon.domain.entity.Coupon;

public class CouponMapper {
    public static Coupon toEntityCreate(CreateCouponRequest request) {
        return Coupon.builder()
                .code(request.code())
                .value(request.value())
                .expiresAt(request.expiresAt())
                .build();
    }

    public static Coupon toEntityUpdate(UpdateCouponRequest request) {
        return Coupon.builder()
                .code(request.code())
                .value(request.value())
                .expiresAt(request.expiresAt())
                .active(request.active())
                .build();
    }

    public static CouponResponse toDto(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getValue(),
                coupon.getExpiresAt(),
                coupon.getActive()
        );
    }
}

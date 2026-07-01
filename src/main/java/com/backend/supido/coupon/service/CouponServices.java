package com.backend.supido.coupon.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.coupon.domain.dto.request.CreateCouponRequest;
import com.backend.supido.coupon.domain.dto.request.UpdateCouponRequest;
import com.backend.supido.coupon.domain.dto.response.CouponResponse;

public interface CouponServices {
    CouponResponse create(CreateCouponRequest request);
    CouponResponse findById(Long id);
    CouponResponse findByCode(String code);
    PageableResponse<CouponResponse> findAll(int page, int size);
    CouponResponse update(Long id, UpdateCouponRequest request);
    CouponResponse toggleActive(Long id);
    void delete(Long id);
}

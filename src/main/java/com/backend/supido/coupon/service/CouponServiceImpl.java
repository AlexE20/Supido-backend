package com.backend.supido.coupon.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.coupon.domain.dto.request.CreateCouponRequest;
import com.backend.supido.coupon.domain.dto.request.UpdateCouponRequest;
import com.backend.supido.coupon.domain.dto.response.CouponResponse;
import com.backend.supido.coupon.domain.entity.Coupon;
import com.backend.supido.coupon.mapper.CouponMapper;
import com.backend.supido.coupon.repository.CouponRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponServices{

    private final CouponRepository couponRepository;

    @Override
    public CouponResponse create(CreateCouponRequest request) {
        if (couponRepository.findByCode(request.code()).isPresent()) {
            throw new IllegalArgumentException("Coupon with code " + request.code() + " already exists");
        }
        Coupon coupon = CouponMapper.toEntityCreate(request);
        coupon.setActive(true);
        return CouponMapper.toDto(couponRepository.save(coupon));
    }

    @Override
    public CouponResponse findById(Long id) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        return CouponMapper.toDto(coupon);
    }

    @Override
    public CouponResponse findByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        return CouponMapper.toDto(coupon);
    }

    @Override
    public PageableResponse<CouponResponse> findAll(int page, int size) {
        Page<Coupon> couponPage = couponRepository.findAll(PageRequest.of(page, size));
        return buildPageableResponse(couponPage);
    }

    @Override
    public CouponResponse update(Long id, UpdateCouponRequest request) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));

        Coupon updated = CouponMapper.toEntityUpdate(request);
        updated.setId(existing.getId());
        return CouponMapper.toDto(couponRepository.save(updated));
    }

    @Override
    public void delete(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        couponRepository.delete(coupon);
    }

    private PageableResponse<CouponResponse> buildPageableResponse(Page<Coupon> couponPage) {
        return PageableResponse.<CouponResponse>builder()
                .content(couponPage.getContent()
                        .stream()
                        .map(CouponMapper::toDto)
                        .collect(Collectors.toList()))
                .page(couponPage.getNumber())
                .size(couponPage.getSize())
                .totalElements(couponPage.getTotalElements())
                .totalPages(couponPage.getTotalPages())
                .last(couponPage.isLast())
                .build();
    }
}

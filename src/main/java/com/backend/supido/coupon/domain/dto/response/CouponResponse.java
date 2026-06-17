package com.backend.supido.coupon.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(

        Long id,
        String code,
        BigDecimal value,
        LocalDateTime expiresAt,
        Boolean active

) {
}

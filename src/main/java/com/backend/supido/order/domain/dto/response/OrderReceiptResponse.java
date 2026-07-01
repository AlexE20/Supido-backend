package com.backend.supido.order.domain.dto.response;

import com.backend.supido.claim.domain.dto.response.ClaimResponse;
import com.backend.supido.payment.domain.dto.response.PaymentResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderReceiptResponse(
        OrderResponse order,
        PaymentResponse payment,
        List<ClaimResponse> claims
) {}

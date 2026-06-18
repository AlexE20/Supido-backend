package com.backend.supido.payment.mapper;

import com.backend.supido.payment.domain.dto.response.PaymentResponse;
import com.backend.supido.payment.domain.entity.Payment;
import com.backend.supido.payment.domain.enums.PaymentMethod;
import com.backend.supido.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;

public class PaymentMapper {

     public static Payment toEntity(Long orderId, PaymentMethod method, PaymentStatus status, BigDecimal amount) {
         return Payment.builder()
                 .orderId(orderId)
                 .method(method)
                 .status(status)
                 .amount(amount)
                 .build();
     }

     public static PaymentResponse toDto(Payment payment) {
         return new PaymentResponse(
                 payment.getId(),
                 payment.getOrderId(),
                 payment.getMethod(),
                 payment.getStatus(),
                 payment.getAmount(),
                 payment.getReference(),
                 payment.getProcessedAt()
                 );
     }
}

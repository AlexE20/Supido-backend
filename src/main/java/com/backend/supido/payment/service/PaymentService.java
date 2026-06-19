package com.backend.supido.payment.service;

import com.backend.supido.payment.domain.dto.response.PaymentResponse;
import com.backend.supido.payment.domain.enums.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentResponse createForOrder(Long orderId, PaymentMethod method, BigDecimal amount);
    void completeCashPayment(Long orderId);
    PaymentResponse findById(Long id);
    PaymentResponse findByOrderId(Long orderId);
    void cancelPayment(Long orderId);
    void markRefunded(Long orderId);
}

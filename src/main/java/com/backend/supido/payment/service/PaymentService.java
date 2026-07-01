package com.backend.supido.payment.service;

import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.payment.domain.dto.response.PaymentResponse;
import com.backend.supido.payment.domain.enums.PaymentMethod;
import com.backend.supido.user.domain.entity.User;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentResponse createForOrder(Order order, PaymentMethod method, BigDecimal amount);
    void completeCashPayment(Long orderId);
    PaymentResponse findById(Long id, User user);
    PaymentResponse findByOrderId(Long orderId, User user);
    void cancelPayment(Long orderId);
    void markRefunded(Long orderId);
}

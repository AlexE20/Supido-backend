package com.backend.supido.payment.service.strategy;

import com.backend.supido.payment.domain.entity.Payment;
import com.backend.supido.payment.domain.enums.PaymentMethod;

public interface PaymentStrategy {
    Payment process(Payment payment);
    PaymentMethod getMethod();
}

package com.backend.supido.payment.service.strategy;

import com.backend.supido.payment.domain.entity.Payment;
import com.backend.supido.payment.domain.enums.PaymentMethod;
import com.backend.supido.payment.domain.enums.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentStrategy implements PaymentStrategy{

    @Override
    public Payment process(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.CASH;
    }
}

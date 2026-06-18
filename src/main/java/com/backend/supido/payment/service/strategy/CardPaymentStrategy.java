package com.backend.supido.payment.service.strategy;

import com.backend.supido.payment.domain.entity.Payment;
import com.backend.supido.payment.domain.enums.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentStrategy implements PaymentStrategy{

    @Override
    public Payment process(Payment payment) {
        throw new UnsupportedOperationException("Pago con tarjeta no disponible todavia");
    }

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.CARD;
    }
}

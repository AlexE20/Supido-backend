package com.backend.supido.payment.service;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.payment.domain.dto.response.PaymentResponse;
import com.backend.supido.payment.domain.entity.Payment;
import com.backend.supido.payment.domain.enums.PaymentMethod;
import com.backend.supido.payment.domain.enums.PaymentStatus;
import com.backend.supido.payment.mapper.PaymentMapper;
import com.backend.supido.payment.repository.PaymentRepository;
import com.backend.supido.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final Map<PaymentMethod, PaymentStrategy> strategies;

    // constructor manual para armar el mapa, en vez de @RequiredArgsConstructor para este campo
    public PaymentServiceImpl(PaymentRepository paymentRepository, List<PaymentStrategy> strategyList) {
        this.paymentRepository = paymentRepository;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getMethod, Function.identity()));
    }

    @Override
    public PaymentResponse createForOrder(Long orderId, PaymentMethod method, BigDecimal amount) {
        Payment payment = PaymentMapper.toEntity(orderId, method, PaymentStatus.PENDING, amount);

        PaymentStrategy strategy = strategies.get(method);
        Payment processed = strategy.process(payment);

        return PaymentMapper.toDto(paymentRepository.save(processed));
    }

    @Override
    public void completeCashPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if (payment.getMethod() == PaymentMethod.CASH && payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProcessedAt(LocalDateTime.now());
            paymentRepository.save(payment);
        }
    }

    @Override
    public PaymentResponse findById(Long id) {
        return PaymentMapper.toDto(paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id)));
    }

    @Override
    public PaymentResponse findByOrderId(Long orderId) {
        return PaymentMapper.toDto(paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId)));
    }

    @Override
    public void cancelPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }
}

package com.backend.supido.payment.service;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.payment.domain.dto.response.PaymentResponse;
import com.backend.supido.payment.domain.entity.Payment;
import com.backend.supido.payment.domain.enums.PaymentMethod;
import com.backend.supido.payment.domain.enums.PaymentStatus;
import com.backend.supido.user.domain.entity.User;
import org.springframework.security.access.AccessDeniedException;
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
    public PaymentResponse createForOrder(Order order, PaymentMethod method, BigDecimal amount) {
        Payment payment = PaymentMapper.toEntity(order, method, PaymentStatus.PENDING, amount);

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
    public PaymentResponse findById(Long id, User user) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        validateOwnership(payment, user);
        return PaymentMapper.toDto(payment);
    }

    @Override
    public PaymentResponse findByOrderId(Long orderId, User user) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));
        validateOwnership(payment, user);
        return PaymentMapper.toDto(payment);
    }

    private void validateOwnership(Payment payment, User user) {
        if (!payment.getOrder().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this payment");
        }
    }

    @Override
    public void cancelPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) payment.setStatus(PaymentStatus.REFUNDED);
        else payment.setStatus(PaymentStatus.CANCELLED);

        paymentRepository.save(payment);
    }

    @Override
    public void markRefunded(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
    }

}

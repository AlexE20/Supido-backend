package com.backend.supido.claim.service;


import com.backend.supido.claim.domain.dto.request.ApproveClaimRequest;
import com.backend.supido.claim.domain.dto.request.CreateClaimRequest;
import com.backend.supido.claim.domain.dto.response.ClaimResponse;
import com.backend.supido.claim.domain.entity.Claim;
import com.backend.supido.claim.domain.enums.ClaimStatus;
import com.backend.supido.claim.mapper.ClaimMapper;
import com.backend.supido.claim.repository.ClaimRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.notification.domain.enums.NotificationType;
import com.backend.supido.notification.service.NotificationService;
import com.backend.supido.order.common.enums.Status;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.payment.service.PaymentService;
import com.backend.supido.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    @Override
    public ClaimResponse create(CreateClaimRequest request, User user) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.orderId()));
        
        if (order.getStatus() != Status.DELIVERED) {
            throw new IllegalArgumentException("Claims can only be filed for delivered orders");
        }

        if (!Objects.equals(order.getUser().getId(), user.getId())) {
            throw new AccessDeniedException("You can only file claims for your own orders");
        }

        Claim claim = ClaimMapper.toEntity(request, user, order);
        return ClaimMapper.toDto(claimRepository.save(claim));
    }

    @Override
    public ClaimResponse approve(Long id, ApproveClaimRequest request) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));

        if (claim.getStatus() == ClaimStatus.APPROVED || claim.getStatus() == ClaimStatus.REJECTED) {
            throw new IllegalArgumentException("Claim has already been resolved");
        }

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setRefundAmount(request.refundAmount());
        Claim saved = claimRepository.save(claim);

        paymentService.markRefunded(saved.getOrder().getId());

        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getOrder().getId(),
                NotificationType.CLAIM_APPROVED, "Your claim has been approved. A refund will be processed.");

        return ClaimMapper.toDto(saved);
    }

    @Override
    public ClaimResponse reject(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));

        if (claim.getStatus() == ClaimStatus.APPROVED || claim.getStatus() == ClaimStatus.REJECTED) {
            throw new IllegalArgumentException("Claim has already been resolved");
        }

        claim.setStatus(ClaimStatus.REJECTED);
        Claim saved = claimRepository.save(claim);

        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getOrder().getId(),
                NotificationType.CLAIM_REJECTED, "Your claim has been reviewed and was not approved.");

        return ClaimMapper.toDto(saved);
    }

    @Override
    public ClaimResponse findById(Long id, User user) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        if (!claim.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this claim");
        }
        return ClaimMapper.toDto(claim);
    }

    @Override
    public List<ClaimResponse> findByOrderId(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!Objects.equals(order.getUser().getId(), user.getId())) {
            throw new AccessDeniedException("You do not have permission to access claims for this order");
        }
        return claimRepository.findByOrder_Id(orderId).stream()
                .map(ClaimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaimResponse> findByUserId(Long userId) {
        return claimRepository.findByUser_Id(userId).stream()
                .map(ClaimMapper::toDto)
                .collect(Collectors.toList());
    }
}


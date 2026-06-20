package com.backend.supido.notification.service;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.notification.domain.dto.response.NotificationResponse;
import com.backend.supido.notification.domain.entities.Notification;
import com.backend.supido.notification.domain.enums.NotificationType;
import com.backend.supido.notification.mapper.NotificationMapper;
import com.backend.supido.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void sendOrderNotification(Long userId, Long orderId, NotificationType type, String message) {
        Notification notification = NotificationMapper.toEntity(userId, orderId, type, message);
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> findByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId)
                .stream().map(NotificationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> findUnreadByUserId(Long userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId)
                .stream().map(NotificationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification with id: " + id + " not found"));
        notification.setRead(true);
        return NotificationMapper.toDto(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadFalse(userId);
        unreadNotifications.forEach(noti -> noti.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public void notifyDeliveryNearby(Long orderId) {

    }

    @Override
    public void notifyNewOrderToDeliveryPersons(List<Long> repartidorUserIds, Long orderId) {
        repartidorUserIds.forEach(repartidorUserId -> sendOrderNotification(repartidorUserId, orderId, NotificationType.NEW_ORDER_AVAILABLE,
                "There is an available order near you."));
    }
}

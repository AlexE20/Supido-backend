package com.backend.supido.notification.service;

import com.backend.supido.notification.domain.dto.response.NotificationResponse;
import com.backend.supido.notification.domain.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void sendOrderNotification(Long userId, Long orderId, NotificationType type, String message);
    List<NotificationResponse> findByUserId(Long userId);
    List<NotificationResponse> findUnreadByUserId(Long userId);
    NotificationResponse markAsRead(Long id);
    void markAllAsRead(Long userId);
    void notifyDeliveryNearby(Long orderId);
    void notifyNewOrderToDeliveryPersons(List<Long> repartidorUserIds, Long orderId);
}

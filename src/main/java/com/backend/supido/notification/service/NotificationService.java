package com.backend.supido.notification.service;

import com.backend.supido.notification.domain.dto.response.NotificationResponse;
import com.backend.supido.notification.domain.enums.NotificationType;
import com.backend.supido.user.domain.entity.User;

import java.util.List;

public interface NotificationService {
    void sendOrderNotification(Long userId, Long orderId, NotificationType type, String message);
    List<NotificationResponse> findByUserId(User user);
    List<NotificationResponse> findUnreadByUserId(User user);
    NotificationResponse markAsRead(Long id, User user);
    void markAllAsRead(User user);
    void notifyDeliveryNearby(Long orderId);
    void notifyNewOrderToDeliveryPersons(List<Long> repartidorUserIds, Long orderId);
}

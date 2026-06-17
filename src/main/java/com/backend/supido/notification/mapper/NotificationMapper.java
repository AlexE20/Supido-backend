package com.backend.supido.notification.mapper;

import com.backend.supido.notification.domain.dto.response.NotificationResponse;
import com.backend.supido.notification.domain.entities.Notification;
import com.backend.supido.notification.domain.enums.NotificationType;

public class NotificationMapper {

    public static Notification toEntity(Long userId, Long orderId, NotificationType type, String message) {
        return Notification.builder()
                .userId(userId)
                .orderId(orderId)
                .type(type)
                .message(message)
                .build();
    }

    public static NotificationResponse toDto(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getOrderId(),
                notification.getType(),
                notification.getMessage(),
                notification.getRead(),
                notification.getSentAt()
        );
    }
}

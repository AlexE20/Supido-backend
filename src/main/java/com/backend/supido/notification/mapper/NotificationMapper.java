package com.backend.supido.notification.mapper;

import com.backend.supido.notification.domain.dto.response.NotificationResponse;
import com.backend.supido.notification.domain.entities.Notification;
import com.backend.supido.notification.domain.enums.NotificationType;
import com.backend.supido.user.domain.entity.User;

public class NotificationMapper {

    public static Notification toEntity(Long orderId, NotificationType type, String message, User user) {
        return Notification.builder()
                .user(user)
                .orderId(orderId)
                .type(type)
                .message(message)
                .build();
    }

    public static NotificationResponse toDto(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                notification.getOrderId(),
                notification.getType(),
                notification.getMessage(),
                notification.getRead(),
                notification.getSentAt()
        );
    }
}

package com.backend.supido.notification.domain.dto.response;

import com.backend.supido.notification.domain.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long userId,
        Long orderId,
        NotificationType type,
        String message,
        Boolean read,
        LocalDateTime sentAt
) { }

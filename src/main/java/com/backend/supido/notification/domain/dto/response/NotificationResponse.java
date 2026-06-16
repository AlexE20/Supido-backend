package com.backend.supido.notification.domain.dto.response;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long userId,
        Long orderId,
        String type,
        String message,
        Boolean read,
        LocalDateTime sentAt
) { }

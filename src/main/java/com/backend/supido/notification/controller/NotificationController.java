package com.backend.supido.notification.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse> findByUserId(@PathVariable Long userId) {
        return buildResponse("Notification retrieved", HttpStatus.OK,
                notificationService.findByUserId(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<GeneralResponse> findUnread(@PathVariable Long userId) {
        return buildResponse("Unread notifications retrieved", HttpStatus.OK,
                notificationService.findUnreadByUserId(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<GeneralResponse> markAsRead(@PathVariable Long id) {
        return buildResponse("Notification marked as read", HttpStatus.OK,
                notificationService.markAsRead(id));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<GeneralResponse> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return buildResponse("All notifications marked as read", HttpStatus.OK, null);
    }


    public ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data){
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build()
                );
    }
}

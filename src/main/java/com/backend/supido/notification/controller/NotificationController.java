package com.backend.supido.notification.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.notification.service.NotificationService;
import com.backend.supido.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<GeneralResponse> findByUserId(@AuthenticationPrincipal User user) {
        return buildResponse("Notification retrieved", HttpStatus.OK,
                notificationService.findByUserId(user));
    }

    @GetMapping("/unread")
    public ResponseEntity<GeneralResponse> findUnread(@AuthenticationPrincipal User user) {
        return buildResponse("Unread notifications retrieved", HttpStatus.OK,
                notificationService.findUnreadByUserId(user));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<GeneralResponse> markAsRead(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return buildResponse("Notification marked as read", HttpStatus.OK,
                notificationService.markAsRead(id,user));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<GeneralResponse> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user);
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

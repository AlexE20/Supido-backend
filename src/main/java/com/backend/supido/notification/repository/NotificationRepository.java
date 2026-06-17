package com.backend.supido.notification.repository;

import com.backend.supido.notification.domain.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
    List<Notification> findByUserIdAndReadFalse(Long userId);
}

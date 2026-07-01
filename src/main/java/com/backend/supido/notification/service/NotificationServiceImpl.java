package com.backend.supido.notification.service;

import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.notification.domain.dto.response.NotificationResponse;
import com.backend.supido.notification.domain.entities.Notification;
import com.backend.supido.notification.domain.enums.NotificationType;
import com.backend.supido.notification.mapper.NotificationMapper;
import com.backend.supido.notification.repository.NotificationRepository;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.user.common.mapper.UserMapper;
import com.backend.supido.user.domain.dto.response.UserResponse;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void sendOrderNotification(Long userId, Long orderId, NotificationType type, String message) {
        User user=userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Notification notification = NotificationMapper.toEntity(orderId, type, message,user);
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> findByUserId(User user) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(user.getId())
                .stream().map(NotificationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> findUnreadByUserId(User user) {
        return notificationRepository.findByUserIdAndReadFalse(user.getId())
                .stream().map(NotificationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public NotificationResponse markAsRead(Long id,User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification with id: " + id + " not found"));
        validate(notification, user);
        notification.setRead(true);
        return NotificationMapper.toDto(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadFalse(user.getId());
        unreadNotifications.forEach(noti -> noti.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public void notifyDeliveryNearby(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id: " + " not found"));

        sendOrderNotification(order.getUser().getId(), orderId, NotificationType.DELIVERY_NEARBY,
                "Your delivery person is nearby. Get ready to receive your order");
    }

    @Override
    public void notifyNewOrderToDeliveryPersons(List<Long> repartidorUserIds, Long orderId) {
        repartidorUserIds.forEach(repartidorUserId -> sendOrderNotification(repartidorUserId, orderId, NotificationType.NEW_ORDER_AVAILABLE,
                "There is an available order near you."));
    }

    public UserResponse getUserById(Long id){
        User user= userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return userMapper.toUserDto(user);
    }

    public void validate(Notification notification, User user){
        if(!notification.getUser().getId().equals(user.getId())){
            throw new ResourceNotFoundException("User is not the owner of this notification");
        }
    }
}

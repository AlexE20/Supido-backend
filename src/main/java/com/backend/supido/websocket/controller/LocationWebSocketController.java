package com.backend.supido.websocket.controller;

import com.backend.supido.common.utils.GeoUtils;
import com.backend.supido.deliveryPerson.service.DeliveryPersonService;
import com.backend.supido.notification.service.NotificationService;
import com.backend.supido.order.common.enums.Status;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.websocket.dto.LocationBroadcast;
import com.backend.supido.websocket.dto.LocationUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class LocationWebSocketController {

    private final Set<Long> notifiedOrders = new HashSet<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final DeliveryPersonService deliveryPersonService;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    /**
     * Drivers send their location to /app/driver/location
     * The backend persists the location and broadcasts to each active order's topic.
     */
    @MessageMapping("/driver/location")
    public void handleLocationUpdate(LocationUpdateMessage message) {
        message.setTimestamp(LocalDateTime.now());

        deliveryPersonService.updateLocation(
                message.getDeliveryPersonId(),
                message.getLatitude(),
                message.getLongitude()
        );

        List<Order> activeOrders = orderRepository.findByDeliveryPersonId(message.getDeliveryPersonId());
        for (Order order : activeOrders) {
            if (isActiveStatus(order.getStatus())) {
                LocationBroadcast broadcast = LocationBroadcast.builder()
                        .orderId(order.getId())
                        .deliveryPersonId(message.getDeliveryPersonId())
                        .latitude(message.getLatitude())
                        .longitude(message.getLongitude())
                        .orderStatus(order.getStatus())
                        .timestamp(message.getTimestamp())
                        .build();

                messagingTemplate.convertAndSend("/topic/tracking/" + order.getId(), broadcast);

                // notificar al usuario cuando esta cerca el deliveryPerson
                double D = GeoUtils.calculateDistanceKm(message.getLatitude(), message.getLongitude(), order.getUserAddress().getLatitude(),
                        order.getUserAddress().getLongitude());
                if (order.getStatus() == Status.ON_THE_WAY && D <= 1 && !notifiedOrders.contains(order.getId())) {
                    notificationService.notifyDeliveryNearby(order.getId());
                    notifiedOrders.add(order.getId());
                }
            }
        }
    }

    private boolean isActiveStatus(Status status) {
        return "CONFIRMED".equals(status.toString()) || "PREPARING".equals(status.toString()) || "ON_THE_WAY".equals(status.toString());
    }
}
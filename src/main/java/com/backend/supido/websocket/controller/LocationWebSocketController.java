package com.backend.supido.websocket.controller;

import com.backend.supido.deliveryPerson.domain.dto.response.DeliveryPersonResponse;
import com.backend.supido.deliveryPerson.service.DeliveryPersonService;
import com.backend.supido.order.common.enums.Status;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.websocket.dto.DriverLocationBroadcast;
import com.backend.supido.websocket.dto.LocationBroadcast;
import com.backend.supido.websocket.dto.LocationUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LocationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final DeliveryPersonService deliveryPersonService;
    private final OrderRepository orderRepository;

    /**
     * Drivers send their location to /app/driver/location
     * The backend persists the location and broadcasts to each active order's topic.
     */
    @MessageMapping("/driver/location")
    public void handleLocationUpdate(LocationUpdateMessage message) {
        message.setTimestamp(LocalDateTime.now());

        DeliveryPersonResponse driver = deliveryPersonService.updateLocation(
                message.getDeliveryPersonId(),
                message.getLatitude(),
                message.getLongitude()
        );

        messagingTemplate.convertAndSend("/topic/drivers/all", DriverLocationBroadcast.builder()
                .deliveryPersonId(message.getDeliveryPersonId())
                .latitude(message.getLatitude())
                .longitude(message.getLongitude())
                .available(driver.available())
                .timestamp(message.getTimestamp())
                .build());

        List<Order> activeOrders = orderRepository.findByDeliveryPerson_Id(message.getDeliveryPersonId());
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
            }
        }
    }

    private boolean isActiveStatus(Status status) {
        return status == Status.CONFIRMED || status == Status.PREPARING || status == Status.ON_THE_WAY;
    }
}
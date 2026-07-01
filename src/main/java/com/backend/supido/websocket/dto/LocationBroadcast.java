package com.backend.supido.websocket.dto;

import com.backend.supido.order.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationBroadcast {
    private Long orderId;
    private Long deliveryPersonId;
    private double latitude;
    private double longitude;
    private Status orderStatus;
    private LocalDateTime timestamp;
}
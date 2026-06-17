package com.backend.supido.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateMessage {
    private Long deliveryPersonId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
}
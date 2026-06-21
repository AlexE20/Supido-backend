package com.backend.supido.userAddress.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressResponse {
    private Long id;
    private String label;
    private String street;
    private String city;
    private Double latitude;
    private Double longitude;
}

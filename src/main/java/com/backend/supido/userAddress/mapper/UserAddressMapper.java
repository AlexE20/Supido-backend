package com.backend.supido.userAddress.mapper;

import com.backend.supido.user.domain.entity.User;
import com.backend.supido.userAddress.domain.dto.request.UserAddressRequest;
import com.backend.supido.userAddress.domain.dto.response.UserAddressResponse;
import com.backend.supido.userAddress.domain.entity.UserAddress;

public class UserAddressMapper {

    public static UserAddress toEntity(UserAddressRequest request, User user) {
        return UserAddress.builder()
                .label(request.getLabel())
                .street(request.getStreet())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .build();
    }

    public static UserAddressResponse toDto(UserAddress address) {
        return UserAddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .street(address.getStreet())
                .city(address.getCity())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }
}

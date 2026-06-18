package com.backend.supido.userAddress.service;

import com.backend.supido.user.domain.entity.User;
import com.backend.supido.userAddress.domain.dto.request.UserAddressRequest;
import com.backend.supido.userAddress.domain.dto.response.UserAddressResponse;

import java.util.List;

public interface UserAddressService {
    UserAddressResponse create(Long userId, UserAddressRequest request, User user);
    UserAddressResponse findById(Long userId, Long addressId, User user);
    UserAddressResponse findByName(Long userId, String addressName,User user);
    List<UserAddressResponse> findAllByUserId(Long userId, User user);
    UserAddressResponse update(Long userId, Long addressId, UserAddressRequest request, User user);
    void delete(Long userId, Long addressId, User user);
}

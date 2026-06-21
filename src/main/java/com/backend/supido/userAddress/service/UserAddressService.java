package com.backend.supido.userAddress.service;

import com.backend.supido.user.domain.entity.User;
import com.backend.supido.userAddress.domain.dto.request.UserAddressRequest;
import com.backend.supido.userAddress.domain.dto.response.UserAddressResponse;

import java.util.List;

public interface UserAddressService {
    UserAddressResponse create(UserAddressRequest request, User user);
    UserAddressResponse findById(Long addressId, User user);
    UserAddressResponse findByName(String addressName,User user);
    List<UserAddressResponse> findAllByUser( User user);
    UserAddressResponse update(Long addressId, UserAddressRequest request, User user);
    void delete(Long addressId, User user);
}

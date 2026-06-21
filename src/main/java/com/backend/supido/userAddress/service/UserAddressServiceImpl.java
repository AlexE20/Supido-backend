package com.backend.supido.userAddress.service;

import com.backend.supido.common.utils.JwtValidator;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.user.repository.UserRepository;
import com.backend.supido.userAddress.domain.dto.request.UserAddressRequest;
import com.backend.supido.userAddress.domain.dto.response.UserAddressResponse;
import com.backend.supido.userAddress.domain.entity.UserAddress;
import com.backend.supido.userAddress.mapper.UserAddressMapper;
import com.backend.supido.userAddress.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;
    private final JwtValidator jwtValidator;

    @Override
    public UserAddressResponse create(Long userId, UserAddressRequest request, User user) {
        User userReq = findUser(userId);
        jwtValidator.validate(userReq, user);
        isAddressExisting(request,user);
        UserAddress address = UserAddressMapper.toEntity(request, userReq);

        return UserAddressMapper.toDto(userAddressRepository.save(address));
    }

    @Override
    public UserAddressResponse findByName(Long userId, String addressName, User user) {
        User userReq = findUser(userId);
        jwtValidator.validate(userReq, user);
        UserAddress address = userAddressRepository.findByLabelAndUserId(addressName, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        return UserAddressMapper.toDto(address);
    }

    @Override
    public UserAddressResponse findById(Long userId, Long addressId, User user) {
        User userReq = findUser(userId);
        jwtValidator.validate(userReq, user);
        return UserAddressMapper.toDto(findAddress(userId, addressId));
    }

    @Override
    public List<UserAddressResponse> findAllByUserId(Long userId, User user) {
        User userReq = findUser(userId);
        jwtValidator.validate(userReq, user);
        return userAddressRepository.findAllByUserId(userId)
                .stream()
                .map(UserAddressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserAddressResponse update(Long userId, Long addressId, UserAddressRequest request, User user) {
        User userReq = findUser(userId);
        jwtValidator.validate(userReq, user);
        isAddressExisting(request,user);
        UserAddress address = findAddress(userId, addressId);
        address.setLabel(request.getLabel());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        return UserAddressMapper.toDto(userAddressRepository.save(address));
    }

    @Override
    public void delete(Long userId, Long addressId, User user) {
        User userReq = findUser(userId);
        jwtValidator.validate(userReq, user);
        userAddressRepository.delete(findAddress(userId, addressId));
    }


    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserAddress findAddress(Long userId, Long addressId) {
        return userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }
    public void isAddressExisting( UserAddressRequest request, User user){
        userAddressRepository.findByLabelAndUserId(request.getLabel(), user.getId()).ifPresent(existingUserAddress -> {
            throw new IllegalArgumentException("The address already exists.");
        });
    }
}

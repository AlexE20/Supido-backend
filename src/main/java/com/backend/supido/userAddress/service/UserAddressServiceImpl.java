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
    public UserAddressResponse create(UserAddressRequest request, User user) {
        User userReq = findUser(user.getId());
        isAddressExisting(request,user);
        UserAddress address = UserAddressMapper.toEntity(request, userReq);

        return UserAddressMapper.toDto(userAddressRepository.save(address));
    }

    @Override
    public UserAddressResponse findByName(String addressName, User user) {
        User userReq = findUser(user.getId());
        UserAddress address = userAddressRepository.findByLabelAndUserId(addressName, userReq.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        return UserAddressMapper.toDto(address);
    }

    @Override
    public UserAddressResponse findById(Long addressId, User user) {
        return UserAddressMapper.toDto(findAddress(user.getId(), addressId));
    }

    @Override
    public List<UserAddressResponse> findAllByUser(User user) {
        User userReq = findUser(user.getId());
        return userAddressRepository.findAllByUserId(userReq.getId())
                .stream()
                .map(UserAddressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserAddressResponse update( Long addressId, UserAddressRequest request, User user) {
        UserAddress address= userAddressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if(!address.getUser().getId().equals(user.getId())){
            throw new ResourceNotFoundException("This user cannot modify this address");
        }
        address.setLabel(request.getLabel());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        return UserAddressMapper.toDto(userAddressRepository.save(address));
    }

    @Override
    public void delete(Long addressId, User user) {
        UserAddress address = userAddressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        User userReq = findUser(user.getId());
        if(!address.getUser().getId().equals(user.getId())){
            throw new ResourceNotFoundException("This user cannot delete this address");
        }
        userAddressRepository.delete(findAddress(user.getId(), addressId));
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

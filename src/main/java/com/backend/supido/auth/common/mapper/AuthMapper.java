package com.backend.supido.auth.common.mapper;

import com.backend.supido.auth.domain.dto.request.RegisterRequest;
import com.backend.supido.user.domain.dto.response.UserResponse;
import com.backend.supido.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
public User toUser(RegisterRequest user){
    return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .email(user.getEmail())
            .phone(user.getPhone())
            .build();
    }

public UserResponse toUserDto(User  user){
    return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .build();
}



}

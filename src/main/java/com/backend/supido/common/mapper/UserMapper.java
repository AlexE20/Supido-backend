package com.backend.supido.common.mapper;

import com.backend.supido.auth.domain.dto.request.auth.RegisterRequest;
import com.backend.supido.auth.domain.dto.response.UserResponse;
import com.backend.supido.auth.domain.entities.User;
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

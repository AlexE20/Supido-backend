package com.backend.supido.auth.services.impl;

import com.backend.supido.common.mapper.UserMapper;
import com.backend.supido.auth.domain.dto.request.auth.UserRequest;
import com.backend.supido.auth.domain.dto.response.UserResponse;
import com.backend.supido.auth.domain.entities.User;
import com.backend.supido.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

    }

    public UserResponse getUserById(Long id){
       User user= userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return userMapper.toUserDto(user);
    }


    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        userRepository.findByUsername(userRequest.getUsername())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(id)) {
                        throw new RuntimeException("El username ya está en uso");
                    }
                });
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    public String deleteUser(Long id){
        UserResponse existingUser=
                this.getUserById(id);
        userRepository.deleteById(id);
        return "El usuario" + existingUser.getUsername() + " ha sido eliminado exitosamente.";
    }


}
package com.backend.supido.user.service;

import com.backend.supido.auth.domain.entity.Role;
import com.backend.supido.auth.repository.RoleRepository;
import com.backend.supido.auth.common.mapper.AuthMapper;
import com.backend.supido.auth.domain.dto.request.RegisterRequest;
import com.backend.supido.user.common.mapper.UserMapper;
import com.backend.supido.user.domain.dto.request.UserRequest;
import com.backend.supido.user.domain.dto.response.UserResponse;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.user.repository.UserRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    }

    public UserResponse getUserById(Long id){
       User user= userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));;
        return userMapper.toUserDto(user);
    }

    public UserResponse createUser(UserRequest userRequest){
        userRepository.findByUsername(userRequest.getUsername()).ifPresent(existingUser -> {
            throw new IllegalArgumentException("Username already exists: " + userRequest.getUsername());
        });
        Role role=roleRepository.findByName(userRequest.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + userRequest.getRole()));
        User user= userMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }


    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        userRepository.findByUsername(userRequest.getUsername())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(id)) {
                        throw new IllegalArgumentException("El username ya está en uso");
                    }
                });

        Role role = roleRepository.findByName(userRequest.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(role);
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    public String deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        userRepository.deleteById(id);
        return "El usuario" + user.getUsername() + " ha sido eliminado exitosamente.";
    }


}
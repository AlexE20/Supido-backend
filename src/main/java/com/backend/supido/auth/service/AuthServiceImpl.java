package com.backend.supido.auth.service;

import com.backend.supido.auth.common.mapper.AuthMapper;
import com.backend.supido.auth.domain.dto.request.LoginRequest;
import com.backend.supido.auth.domain.dto.request.RegisterRequest;
import com.backend.supido.auth.domain.dto.response.AuthResponse;
import com.backend.supido.auth.domain.entity.Role;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.auth.repository.RoleRepository;
import com.backend.supido.user.repository.UserRepository;
import com.backend.supido.common.utils.JwtUtil;
import com.backend.supido.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El username ya está en uso");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        User user = authMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        String token = jwtUtil.generateToken(Map.of("role", user.getRole().getName()), user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(Map.of("role", user.getRole().getName()), user);
        return new AuthResponse(token);
    }
}
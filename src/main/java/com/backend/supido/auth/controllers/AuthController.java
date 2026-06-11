package com.backend.supido.auth.controllers;

import com.backend.supido.auth.domain.dto.request.auth.UserLoginRequest;
import com.backend.supido.auth.domain.dto.request.auth.UserRequest;
import com.backend.supido.auth.domain.dto.response.AuthResponse;
import com.backend.supido.auth.services.AuthService;
import com.backend.supido.auth.services.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(authService.register(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        return ResponseEntity.ok(authService.login(userLoginRequest));
    }
}

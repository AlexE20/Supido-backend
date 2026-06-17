package com.backend.supido.auth.controller;

import com.backend.supido.auth.domain.dto.request.LoginRequest;
import com.backend.supido.auth.domain.dto.request.RegisterRequest;
import com.backend.supido.auth.domain.dto.response.AuthResponse;
import com.backend.supido.auth.service.AuthServiceImpl;
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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest userLoginRequest) {
        return ResponseEntity.ok(authService.login(userLoginRequest));
    }
}

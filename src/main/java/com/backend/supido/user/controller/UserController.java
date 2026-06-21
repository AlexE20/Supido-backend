package com.backend.supido.user.controller;

import com.backend.supido.auth.domain.dto.request.RegisterRequest;
import com.backend.supido.common.GeneralResponse;
import com.backend.supido.user.domain.dto.request.UserRequest;
import com.backend.supido.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<GeneralResponse> getUserById(@PathVariable Long id) {
        return buildResponse("User retrieved successfully", HttpStatus.OK, userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<GeneralResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        return buildResponse("User created successfully", HttpStatus.CREATED, userService.createUser(userRequest));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<GeneralResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        return buildResponse("User updated successfully", HttpStatus.OK, userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<GeneralResponse> deleteUser(@PathVariable Long id) {
        return buildResponse("User deleted successfully", HttpStatus.OK, userService.deleteUser(id));
    }




    public ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data){
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build()
                );
    }
}

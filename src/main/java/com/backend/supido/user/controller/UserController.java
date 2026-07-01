package com.backend.supido.user.controller;

import com.backend.supido.common.GeneralResponse;
import com.backend.supido.user.domain.dto.request.ChangeRoleRequest;
import com.backend.supido.user.domain.dto.request.UpdateUserRequest;
import com.backend.supido.user.domain.dto.request.UserRequest;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMe(@AuthenticationPrincipal User user) {
        return buildResponse("User retrieved successfully", HttpStatus.OK, userService.getMe(user));
    }

    @PreAuthorize("hasRole('SUPER')")
    @GetMapping
    public ResponseEntity<GeneralResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role) {
        if (role != null) {
            return buildResponse("Users retrieved successfully", HttpStatus.OK, userService.findByRole(role, page, size));
        }
        return buildResponse("Users retrieved successfully", HttpStatus.OK, userService.findAll(page, size));
    }

    @PreAuthorize("hasRole('SUPER')")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getUserById(@PathVariable Long id) {
        return buildResponse("User retrieved successfully", HttpStatus.OK, userService.getUserById(id));
    }

    @PreAuthorize("hasRole('SUPER')")
    @PostMapping
    public ResponseEntity<GeneralResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        return buildResponse("User created successfully", HttpStatus.CREATED, userService.createUser(userRequest));
    }

    @PreAuthorize("hasRole('SUPER')")
    @PatchMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return buildResponse("User updated successfully", HttpStatus.OK, userService.updateUser(id, request));
    }

    @PreAuthorize("hasRole('SUPER')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<GeneralResponse> changeRole(@PathVariable Long id, @Valid @RequestBody ChangeRoleRequest request) {
        return buildResponse("Role updated successfully", HttpStatus.OK, userService.changeRole(id, request));
    }

    @PreAuthorize("hasRole('SUPER')")
    @DeleteMapping("/{id}")
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

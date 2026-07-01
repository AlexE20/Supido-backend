package com.backend.supido.user.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeRoleRequest(
        @NotBlank(message = "role is required")
        String role
) {}
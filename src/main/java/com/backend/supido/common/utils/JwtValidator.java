package com.backend.supido.common.utils;

import com.backend.supido.user.domain.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {
    public void validate(User userReq, User user) {
        if (user == null || !user.getId().equals(userReq.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
    }
}

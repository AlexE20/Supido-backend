package com.backend.supido.user.service;

import com.backend.supido.auth.domain.entity.Role;
import com.backend.supido.auth.repository.RoleRepository;
import com.backend.supido.common.PageableResponse;
import com.backend.supido.user.common.mapper.UserMapper;
import com.backend.supido.user.domain.dto.request.ChangeRoleRequest;
import com.backend.supido.user.domain.dto.request.UpdateUserRequest;
import com.backend.supido.user.domain.dto.request.UserRequest;
import com.backend.supido.user.domain.dto.response.UserResponse;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.user.repository.UserRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
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


    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getUsername() != null) {
            userRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
                if (!existing.getId().equals(id))
                    throw new IllegalArgumentException("Username already in use");
            });
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        return userMapper.toUserDto(userRepository.save(user));
    }

    public UserResponse changeRole(Long id, ChangeRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.role()));
        user.setRole(role);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public PageableResponse<UserResponse> findAll(int page, int size) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        return buildPageable(userPage);
    }

    public PageableResponse<UserResponse> findByRole(String role, int page, int size) {
        Page<User> userPage = userRepository.findByRole_Name(role, PageRequest.of(page, size));
        return buildPageable(userPage);
    }

    public UserResponse getMe(User user) {
        return userMapper.toUserDto(user);
    }

    public String deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
        return "User " + user.getUsername() + " deleted successfully.";
    }

    private PageableResponse<UserResponse> buildPageable(Page<User> page) {
        return PageableResponse.<UserResponse>builder()
                .content(page.getContent().stream().map(userMapper::toUserDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
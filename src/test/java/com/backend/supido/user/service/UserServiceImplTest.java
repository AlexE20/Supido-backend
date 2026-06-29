package com.backend.supido.user.service;

import com.backend.supido.auth.domain.entity.Role;
import com.backend.supido.auth.repository.RoleRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.user.common.mapper.UserMapper;
import com.backend.supido.user.domain.dto.request.UserRequest;
import com.backend.supido.user.domain.dto.response.UserResponse;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Role role;
    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        role = Role.builder().id(1L).name("ROLE_USER").build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@email.com")
                .phone("123456789")
                .role(role)
                .build();

        userRequest = UserRequest.builder()
                .username("testuser")
                .password("rawPassword")
                .email("test@email.com")
                .phone("123456789")
                .role("ROLE_USER")
                .build();

        userResponse = UserResponse.builder().id(1L).username("testuser").build();
    }

    // ─── loadUserByUsername ────────────────────────────────────────────────────

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("returns UserDetails when username exists")
        void shouldReturnUserDetails_whenUsernameExists() {
            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

            var result = userService.loadUserByUsername("testuser");

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("throws UsernameNotFoundException when username not found")
        void shouldThrowUsernameNotFoundException_whenUsernameNotFound() {
            given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.loadUserByUsername("unknown"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("unknown");
        }
    }

    // ─── getUserById ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("returns UserResponse when user exists")
        void shouldReturnUserResponse_whenUserExists() {
            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userMapper.toUserDto(user)).willReturn(userResponse);

            UserResponse result = userService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user does not exist")
        void shouldThrowResourceNotFoundException_whenUserNotFound() {
            given(userRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ─── createUser ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("creates and returns UserResponse when data is valid")
        void shouldCreateUser_whenDataIsValid() {
            given(userRepository.findByUsername("testuser")).willReturn(Optional.empty());
            given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(role));
            given(userMapper.toUser(userRequest)).willReturn(user);
            given(passwordEncoder.encode("rawPassword")).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(user);
            given(userMapper.toUserDto(user)).willReturn(userResponse);

            UserResponse result = userService.createUser(userRequest);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("throws IllegalArgumentException when username already exists")
        void shouldThrowIllegalArgumentException_whenUsernameAlreadyExists() {
            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.createUser(userRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("testuser");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when role does not exist")
        void shouldThrowResourceNotFoundException_whenRoleNotFound() {
            given(userRepository.findByUsername("testuser")).willReturn(Optional.empty());
            given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.createUser(userRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("ROLE_USER");

            verify(userRepository, never()).save(any());
        }
    }

    // ─── updateUser ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("updates and returns UserResponse when data is valid")
        void shouldUpdateUser_whenDataIsValid() {
            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
            given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(role));
            given(passwordEncoder.encode("rawPassword")).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(user);
            given(userMapper.toUserDto(user)).willReturn(userResponse);

            UserResponse result = userService.updateUser(1L, userRequest);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user does not exist")
        void shouldThrowResourceNotFoundException_whenUserNotFound() {
            given(userRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(99L, userRequest))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when username belongs to a different user")
        void shouldThrowIllegalArgumentException_whenUsernameTakenByAnotherUser() {
            User anotherUser = User.builder().id(2L).username("testuser").build();

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.findByUsername("testuser")).willReturn(Optional.of(anotherUser));

            assertThatThrownBy(() -> userService.updateUser(1L, userRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("username");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when role does not exist during update")
        void shouldThrowResourceNotFoundException_whenRoleNotFoundOnUpdate() {
            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
            given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(1L, userRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Rol");

            verify(userRepository, never()).save(any());
        }
    }

    // ─── deleteUser ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("deletes user and returns confirmation message")
        void shouldDeleteUser_andReturnMessage() {
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            String result = userService.deleteUser(1L);

            assertThat(result).contains("testuser");
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user does not exist")
        void shouldThrowResourceNotFoundException_whenUserNotFound() {
            given(userRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).deleteById(anyLong());
        }
    }
}
package com.habittracker.service.impl;

import com.habittracker.domain.Role;
import com.habittracker.domain.User;
import com.habittracker.exception.InvalidCredentialsException;
import com.habittracker.exception.UserAlreadyExistsException;
import com.habittracker.generated.model.AuthResponse;
import com.habittracker.generated.model.LoginRequest;
import com.habittracker.generated.model.RegisterRequest;
import com.habittracker.repository.UserRepository;
import com.habittracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach   //all tests will use same request
    void setUp() {

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("lama");
        registerRequest.setEmail("lama@test.com");
        registerRequest.setPassword("123456");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("lama");
        loginRequest.setPassword("123456");

        user = User.builder()
                .id(1L)
                .username("lama")
                .email("lama@test.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        // Arrange (prepare everything you need for the test)
        when(userRepository.existsByUsername("lama"))
                .thenReturn(false);

        when(userRepository.existsByEmail("lama@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("jwt-token");

        // Act (do the actual action)
        AuthResponse response = userService.register(registerRequest);

        // Assert (check if the outcome matches expectations)
        assertNotNull(response);
        assertEquals("lama", response.getUsername());
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUsernameExists() {

        // Arrange
        when(userRepository.existsByUsername("lama"))
                .thenReturn(true);

        // Act + Assert
        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.register(registerRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {

        // Arrange
        when(userRepository.existsByUsername("lama"))
                .thenReturn(false);

        when(userRepository.existsByEmail("lama@test.com"))
                .thenReturn(true);

        // Act + Assert
        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.register(registerRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encodedPassword"))
                .thenReturn(true);

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("jwt-token");

        // Act
        AuthResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("lama", response.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(loginRequest)
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encodedPassword"))
                .thenReturn(false);

        // Act + Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(loginRequest)
        );
    }
}
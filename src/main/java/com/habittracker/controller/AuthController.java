package com.habittracker.controller;

import com.habittracker.generated.model.AuthResponse;
import com.habittracker.generated.model.LoginRequest;
import com.habittracker.generated.model.RegisterRequest;
import com.habittracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Implements the "Auth" paths defined in the OpenAPI contract
 * (src/main/resources/openapi/habit-tracker-api.yaml), using the
 * request/response DTOs generated from that contract.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Registration and login")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(operationId = "registerUser", summary = "Register a new user")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(operationId = "loginUser", summary = "Authenticate and obtain a JWT")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}

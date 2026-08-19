package org.internship.dailyhabittracker.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response returned after successful login or registration (US-001, US-002).
 * Carries the auth token the client must send on subsequent requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String tokenType; // e.g. "Bearer"
    private UserResponse user;
}
package org.internship.dailyhabittracker.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.internship.dailyhabittracker.domain.depricated.User;
import org.internship.dailyhabittracker.domain.models.Role;

import java.time.LocalDateTime;

/**
 * Outward-facing view of a User. Deliberately excludes the password hash.
 * Used in AuthResponse and by admin "view all users" endpoints (US-003).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User saved) {
    }
}
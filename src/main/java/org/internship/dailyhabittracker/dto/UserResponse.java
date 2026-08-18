package org.internship.dailyhabittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.internship.dailyhabittracker.domain.Role;
import org.internship.dailyhabittracker.domain.User;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private Role role;



    public static UserResponse fromEntity(User user){
        return new UserResponse(
                user.getId(),
                user.getUserName(),
                user.getRole()
        );
    }
}

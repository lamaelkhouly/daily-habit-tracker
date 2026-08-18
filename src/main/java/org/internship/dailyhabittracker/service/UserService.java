package org.internship.dailyhabittracker.service;

import org.internship.dailyhabittracker.dto.UserRequest;
import org.internship.dailyhabittracker.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse registerUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    void deactivateUser(Long id);
}

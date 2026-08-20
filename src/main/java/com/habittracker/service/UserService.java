package com.habittracker.service;

import com.habittracker.generated.model.AuthResponse;
import com.habittracker.generated.model.LoginRequest;
import com.habittracker.generated.model.RegisterRequest;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}

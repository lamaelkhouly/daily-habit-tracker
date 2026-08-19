package org.internship.dailyhabittracker.service;

import lombok.RequiredArgsConstructor;
import org.internship.dailyhabittracker.domain.depricated.Role;
import org.internship.dailyhabittracker.domain.depricated.User;
import org.internship.dailyhabittracker.dto.UserRequest;
import org.internship.dailyhabittracker.dto.UserResponse;
import org.internship.dailyhabittracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse registerUser(UserRequest request){
        User user = new User();
        user.setUserName(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(Role.USER);
        user.setActive(true);

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null)
            return null;
        return UserResponse.fromEntity(user);

    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponse::fromEntity).toList();
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user != null){
            user.setActive(false);
            userRepository.save(user);
        }

    }


}

package org.internship.dailyhabittracker.mapper;

import org.internship.dailyhabittracker.domain.depricated.User;
import org.internship.dailyhabittracker.dto.UserRequest;
import org.internship.dailyhabittracker.dto.UserResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel="spring")
public interface UserMapper {
    User toEntity(UserRequest userRequest);
    UserResponse toResponse(User user);
}

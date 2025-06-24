package com.ynov.mehdi.service;

import com.ynov.mehdi.dto.UserDto;
import com.ynov.mehdi.entity.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createUser(User user);

    UserDto updateUser(Long id, User user);

    void deleteUser(Long id);
}

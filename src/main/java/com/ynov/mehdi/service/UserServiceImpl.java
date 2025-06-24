package com.ynov.mehdi.service;

import com.ynov.mehdi.dto.UserDto;
import com.ynov.mehdi.entity.User;
import com.ynov.mehdi.exception.DataIntegrityViolationException;
import com.ynov.mehdi.exception.ObjectNotFoundException;
import com.ynov.mehdi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return toDto(user);
    }

    @Override
    public UserDto createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    public UserDto updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        if (!existing.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        User saved = userRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
        userRepository.delete(existing);
    }
}

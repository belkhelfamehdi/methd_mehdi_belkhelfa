package com.ynov.mehdi.service;

import com.ynov.mehdi.dto.UserDto;
import com.ynov.mehdi.entity.User;
import com.ynov.mehdi.exception.DataIntegrityViolationException;
import com.ynov.mehdi.exception.ObjectNotFoundException;
import com.ynov.mehdi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("pwd")
                .build();
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> result = userService.getAllUsers();
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto dto = userService.getUserById(1L);
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void createUser_success() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto dto = userService.createUser(user);
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    void createUser_duplicateEmail() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto dto = userService.updateUser(1L, user);
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    void updateUser_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void updateUser_duplicateEmail() {
        User other = User.builder().id(2L).name("Jane").email("jane@example.com").password("pwd").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(other.getEmail())).thenReturn(true);
        assertThrows(DataIntegrityViolationException.class, () -> userService.updateUser(1L, other));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(1L));
    }
}

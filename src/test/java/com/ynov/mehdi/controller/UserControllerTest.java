package com.ynov.mehdi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ynov.mehdi.entity.User;
import com.ynov.mehdi.exception.DataIntegrityViolationException;
import com.ynov.mehdi.exception.ObjectNotFoundException;
import com.ynov.mehdi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getUserById_notFound() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new ObjectNotFoundException("not found"));
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_duplicateEmail() throws Exception {
        User user = User.builder().name("test").email("test@example.com").password("pwd").build();
        when(userService.createUser(any(User.class))).thenThrow(new DataIntegrityViolationException("exists"));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}

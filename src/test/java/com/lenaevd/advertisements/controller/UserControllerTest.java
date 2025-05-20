package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.user.ChangeUserRequest;
import com.lenaevd.advertisements.dto.user.UserDto;
import com.lenaevd.advertisements.mapper.UserMapper;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserDto userDto;
    private List<User> users;
    private List<UserDto> userDtos;
    private final int id = 1;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(id);
        user.setUsername("username");
        userDto = new UserDto(id, "username");
        users = List.of(user);
        userDtos = List.of(userDto);
    }

    @Test
    void changeUserInfo() {
        //GIVEN
        Principal principal = mock(Principal.class);
        ChangeUserRequest request = new ChangeUserRequest("newUsername", null, null);

        //WHEN
        ResponseEntity<Void> response = userController.changeUserInfo(request, principal);

        //THEN
        verify(userService).changeUserInfo(principal, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getUsers() {
        //GIVEN
        when(userService.getAll()).thenReturn(users);
        when(mapper.usersToUserDtos(users)).thenReturn(userDtos);

        //WHEN
        ResponseEntity<List<UserDto>> response = userController.getUsers();

        //THEN
        verify(userService).getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDtos, response.getBody());
    }

    @Test
    void getUserById() {
        //GIVEN
        when(userService.getUserById(id)).thenReturn(user);
        when(mapper.userToUserDto(user)).thenReturn(userDto);

        //WHEN
        ResponseEntity<UserDto> response = userController.getUserById(id);

        //THEN
        verify(userService).getUserById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void deleteUser() {
        //WHEN
        ResponseEntity<Void> response = userController.deleteUser(id);

        //THEN
        verify(userService).delete(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

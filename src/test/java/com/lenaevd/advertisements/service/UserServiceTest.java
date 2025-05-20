package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.authentication.CustomUserDetails;
import com.lenaevd.advertisements.dao.UserDao;
import com.lenaevd.advertisements.dto.user.ChangeUserRequest;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final int id = 10;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(id);
        user.setUsername(NAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
    }

    @Test
    void loadUserByUsername() {
        //GIVEN
        when(userDao.findByUsername(NAME)).thenReturn(Optional.of(user));

        //WHEN
        CustomUserDetails resultUser = userService.loadUserByUsername(NAME);

        //THEN
        verify(userDao).findByUsername(NAME);
        assertEquals(NAME, resultUser.getUsername());
        assertEquals(PASSWORD, resultUser.getPassword());
        assertEquals(EMAIL, resultUser.getEmail());
    }

    @Test
    void whenLoadUserByUsernameAndUserNotFoundThrowException() {
        //GIVEN
        when(userDao.findByUsername(NAME)).thenReturn(Optional.empty());

        //WHEN | THEN
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(NAME));

        assertEquals("There is no user with name = " + NAME, exception.getMessage());
        verify(userDao).findByUsername(NAME);
    }

    @Test
    void delete() {
        //GIVEN
        when(userDao.findById(id)).thenReturn(Optional.of(user));

        //WHEN
        userService.delete(id);

        //THEN
        verify(userDao).delete(user);
    }

    @Test
    void getUserById() {
        //GIVEN
        when(userDao.findById(id)).thenReturn(Optional.of(user));

        //WHEN
        User resultUser = userService.getUserById(id);

        //THEN
        assertEquals(user, resultUser);
    }

    @Test
    void getUserByIdAndUserNotFoundThrowException() {
        //GIVEN
        when(userDao.findById(id)).thenReturn(Optional.empty());

        //WHEN | THEN
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userService.getUserById(id));
        assertEquals((new ObjectNotFoundException(id, EntityName.USER)).getMessage(), exception.getMessage());
    }

    @Test
    void getUserFromPrincipal() {
        //GIVEN
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(NAME);
        when(userDao.findByUsername(NAME)).thenReturn(Optional.of(user));

        //WHEN
        User resultUser = userService.getUserFromPrincipal(principal);

        //THEN
        assertEquals(user, resultUser);
    }

    @Test
    void getUserFromPrincipalAndUsernameNotFoundThrowException() {
        //GIVEN
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(NAME);
        when(userDao.findByUsername(NAME)).thenReturn(Optional.empty());

        //WHEN | THEN
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserFromPrincipal(principal);
        });
        assertEquals((new UsernameNotFoundException("There is no user with name = " + principal.getName())).getMessage(),
                exception.getMessage());
    }

    @Test
    void changeUserInfo() {
        //GIVEN
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(NAME);

        ChangeUserRequest request = new ChangeUserRequest("lena", "passMe!!", "newEmail");
        when(userDao.findByUsername(NAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(request.password())).thenReturn(request.password());

        //WHEN
        userService.changeUserInfo(principal, request);

        //THEN
        assertEquals(request.username(), user.getUsername());
        assertEquals(request.password(), user.getPassword());
        assertEquals(request.email(), user.getEmail());

        verify(userDao).update(user);
    }

    @Test
    void changeUserInfoWhenNothingToUpdate() {
        //GIVEN
        Principal principal = mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(NAME);

        ChangeUserRequest request = new ChangeUserRequest(null, null, null);
        when(userDao.findByUsername(NAME)).thenReturn(Optional.of(user));

        //WHEN
        userService.changeUserInfo(principal, request);

        //THEN
        assertEquals(NAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
        assertEquals(EMAIL, user.getEmail());

        verify(userDao, never()).update(user);
    }
}

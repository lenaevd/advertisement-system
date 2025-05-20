package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.dao.UserDao;
import com.lenaevd.advertisements.dto.user.RegisterRequest;
import com.lenaevd.advertisements.exception.UserAlreadyExistsException;
import com.lenaevd.advertisements.exception.WrongCredentialsException;
import com.lenaevd.advertisements.model.Role;
import com.lenaevd.advertisements.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(NAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
    }

    @Test
    void register() {
        //GIVEN
        RegisterRequest request = new RegisterRequest(NAME, EMAIL, PASSWORD, Role.ROLE_USER);
        when(userDao.findByUsername(NAME)).thenReturn(Optional.empty());

        //WHEN
        authenticationService.register(request);

        //THEN
        verify(userDao).save(any(User.class));
    }

    @Test
    void whenRegisterAndUserExistsThrowException() {
        //GIVEN
        RegisterRequest request = new RegisterRequest(NAME, EMAIL, PASSWORD, Role.ROLE_USER);
        when(userDao.findByUsername(NAME)).thenReturn(Optional.of(user));

        //WHEN | THEN
        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(request));
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void whenRegisterAndEmailExistsThrowException() {
        //GIVEN
        RegisterRequest request = new RegisterRequest(NAME, EMAIL, PASSWORD, Role.ROLE_USER);
        when(userDao.findByUsername(NAME)).thenReturn(Optional.empty());
        when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        //WHEN | THEN
        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(request));
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void authenticate() {
        //GIVEN
        when(userDao.findByUsername(NAME)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token");

        //WHEN
        String resultToken = authenticationService.authenticate(NAME, PASSWORD);

        //THEN
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user);
        assertEquals("token", resultToken);
    }

    @Test
    void whenAuthenticateAndUserNotFoundThrowException() {
        //GIVEN
        when(userDao.findByUsername(NAME)).thenReturn(Optional.empty());

        //WHEN | THEN
        WrongCredentialsException exception = assertThrows(WrongCredentialsException.class,
                () -> authenticationService.authenticate(NAME, PASSWORD));
        assertEquals("Wrong username", exception.getMessage());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void whenAuthenticateAndWrongPasswordThrowException() {
        //GIVEN
        when(userDao.findByUsername(NAME)).thenReturn(Optional.of(user));
        doThrow(BadCredentialsException.class)
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        //WHEN | THEN
        WrongCredentialsException exception = assertThrows(WrongCredentialsException.class,
                () -> authenticationService.authenticate(NAME, PASSWORD));
        assertEquals("Wrong password", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }
}

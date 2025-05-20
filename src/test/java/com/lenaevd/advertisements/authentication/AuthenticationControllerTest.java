package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.dto.user.LoginRequest;
import com.lenaevd.advertisements.dto.user.LoginResponse;
import com.lenaevd.advertisements.dto.user.RegisterRequest;
import com.lenaevd.advertisements.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void register() {
        //GIVEN
        RegisterRequest request = new RegisterRequest("username", "email", "password", Role.ROLE_USER);

        //WHEN
        ResponseEntity<Void> response = authenticationController.register(request);

        //THEN
        verify(authenticationService).register(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void authenticate() {
        //GIVEN
        LoginRequest request = new LoginRequest("username", "password");
        String expectedToken = "token";
        when(authenticationService.authenticate(request.username(), request.password())).thenReturn(expectedToken);

        //WHEN
        ResponseEntity<LoginResponse> response = authenticationController.authenticate(request);

        //THEN
        verify(authenticationService).authenticate(request.username(), request.password());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedToken, response.getBody().token());
    }
}

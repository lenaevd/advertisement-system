package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.dto.LoginRequest;
import com.lenaevd.advertisements.dto.LoginResponse;
import com.lenaevd.advertisements.dto.RegisterRequest;
import com.lenaevd.advertisements.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Validated RegisterRequest request) {
        User user = new User(request.username(), request.email(), request.password(), request.role());
        authenticationService.register(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Validated LoginRequest request) {
        String token = authenticationService.authenticate(request.username(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

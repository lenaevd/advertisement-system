package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.dto.user.LoginRequest;
import com.lenaevd.advertisements.dto.user.LoginResponse;
import com.lenaevd.advertisements.dto.user.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "endpoints for register and login users")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Register user")
    public ResponseEntity<Void> register(@RequestBody @Validated RegisterRequest request) {
        authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Validated LoginRequest request) {
        String token = authenticationService.authenticate(request.username(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

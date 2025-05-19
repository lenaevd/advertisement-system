package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.dto.user.RegisterRequest;

public interface AuthenticationService {
    void register(RegisterRequest request);

    String authenticate(String username, String password);
}

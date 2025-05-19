package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.authentication.CustomUserDetails;
import com.lenaevd.advertisements.dto.user.ChangeUserRequest;
import com.lenaevd.advertisements.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;
import java.util.List;

public interface UserService extends UserDetailsService {
    @Override
    CustomUserDetails loadUserByUsername(String username);

    List<User> getAll();

    void delete(int id);

    User getUserFromPrincipal(Principal principal);

    User getUserById(int id);

    void changeUserInfo(Principal principal, ChangeUserRequest request);
}

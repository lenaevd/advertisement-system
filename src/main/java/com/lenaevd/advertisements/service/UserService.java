package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.authentication.CustomUserDetails;
import com.lenaevd.advertisements.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;
import java.util.List;

public interface UserService extends UserDetailsService {
    @Override
    CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    List<User> getAll();

    void delete(int id);

    User getUserFromPrincipal(Principal principal);

    User getUserById(int id);

    void changeUsername(Principal principal, String newUsername);

    void changePassword(Principal principal, String newPassword);

    void changeEmail(Principal principal, String newEmail);
}

package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.authentication.CustomUserDetails;
import com.lenaevd.advertisements.dao.UserDao;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(User object) {
        userDao.save(object);
    }

    @Transactional(readOnly = true)
    public Optional<User> getById(int id) {
        return userDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userDao.findAll();
    }

    @Transactional
    public void delete(int id) {
        userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("There is no user with name = " + username));
    }

    @Transactional(readOnly = true)
    public User getUserFromPrincipal(Principal principal) {
        Optional<User> userOptional = userDao.findByUsername(principal.getName());
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("There is no user with name = " + principal.getName()));
    }

    @Transactional(readOnly = true)
    public User getUserByIdIfExists(int id) {
        return userDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.USER));
    }

    @Transactional
    public void changeUsername(Principal principal, String newUsername) {
        User user = getUserFromPrincipal(principal);
        user.setUsername(newUsername);
        userDao.update(user);
    }

    @Transactional
    public void changePassword(Principal principal, String newPassword) {
        User user = getUserFromPrincipal(principal);
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.update(user);
    }

    @Transactional
    public void changeEmail(Principal principal, String newEmail) {
        User user = getUserFromPrincipal(principal);
        user.setEmail(newEmail);
        userDao.update(user);
    }
}

package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.authentication.CustomUserDetails;
import com.lenaevd.advertisements.dao.impl.UserDaoImpl;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDaoImpl userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("There is no user with name = " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userDao.findAll();
    }

    @Override
    @Transactional
    public void delete(int id) {
        User user = getUserById(id);
        userDao.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserFromPrincipal(Principal principal) {
        Optional<User> userOptional = userDao.findByUsername(principal.getName());
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("There is no user with name = " + principal.getName()));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(int id) {
        return userDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.USER));
    }

    @Override
    @Transactional
    public void changeUsername(Principal principal, String newUsername) {
        User user = getUserFromPrincipal(principal);
        user.setUsername(newUsername);
        userDao.update(user);
    }

    @Override
    @Transactional
    public void changePassword(Principal principal, String newPassword) {
        User user = getUserFromPrincipal(principal);
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.update(user);
    }

    @Override
    @Transactional
    public void changeEmail(Principal principal, String newEmail) {
        User user = getUserFromPrincipal(principal);
        user.setEmail(newEmail);
        userDao.update(user);
    }
}

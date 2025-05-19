package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.authentication.CustomUserDetails;
import com.lenaevd.advertisements.config.LoggerMessages;
import com.lenaevd.advertisements.dao.UserDao;
import com.lenaevd.advertisements.dto.user.ChangeUserRequest;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String NAME_NOT_FOUND = "There is no user with name = ";

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String username) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_USER, "loadUserByUsername", username);
        Optional<User> userOptional = userDao.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException(NAME_NOT_FOUND + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        LOGGER.debug(LoggerMessages.ALL_OBJECTS_REQUESTED);
        return userDao.findAll();
    }

    @Override
    @Transactional
    public void delete(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "delete", EntityName.USER, id);
        User user = getUserById(id);
        userDao.delete(user);
        LOGGER.info(LoggerMessages.DELETE_COMPLETED, EntityName.USER, id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserFromPrincipal(Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_USER, "getUserFromPrincipal", principal.getName());
        Optional<User> userOptional = userDao.findByUsername(principal.getName());
        return userOptional.orElseThrow(() -> new UsernameNotFoundException(NAME_NOT_FOUND + principal.getName()));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "getById", EntityName.USER, id);
        return userDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.USER));
    }

    @Override
    @Transactional
    public void changeUserInfo(Principal principal, ChangeUserRequest request) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_USER, "changeUserInfo", principal.getName());
        User user = getUserFromPrincipal(principal);
        boolean updated = false;
        if (request.username() != null) {
            user.setUsername(request.username());
            updated = true;
        }
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
            updated = true;
        }
        if (request.email() != null) {
            user.setEmail(request.email());
            updated = true;
        }
        if (updated) {
            userDao.update(user);
            LOGGER.info(LoggerMessages.UPDATE_COMPLETED, EntityName.USER, user.getId());
        }
    }
}

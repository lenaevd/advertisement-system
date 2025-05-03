package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.authentication.CustomUserDetails;
import com.lenaevd.advertisements.dao.UserDao;
import com.lenaevd.advertisements.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public void save(User object) {
        userDao.save(object);
    }

    public Optional<User> getById(int id) {
        return userDao.findById(id);
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    @Transactional
    public void delete(int id) {
        userDao.deleteById(id);
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("There is no user with name = " + username));
    }
}

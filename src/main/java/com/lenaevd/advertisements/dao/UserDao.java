package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.User;

import java.util.Optional;

public interface UserDao extends Dao<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}

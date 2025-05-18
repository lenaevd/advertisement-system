package com.lenaevd.advertisements.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    void save(T object);

    void update(T object);

    Optional<T> findById(int id);

    List<T> findAll();

    void delete(T object);
}

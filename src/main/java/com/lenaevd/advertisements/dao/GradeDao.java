package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Grade;

import java.util.List;
import java.util.Optional;

public interface GradeDao extends Dao<Grade> {
    Optional<Grade> findByUsers(int sellerId, int customerId);

    List<Grade> findBySellerId(int sellerId);
}

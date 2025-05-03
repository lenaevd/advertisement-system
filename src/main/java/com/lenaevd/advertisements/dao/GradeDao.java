package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Grade;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class GradeDao extends Dao<Grade> {
    public static final String FIND_ALL_GRADES_QUERY = "SELECT grade FROM Grade grade";

    protected GradeDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<Grade> getEntityClass() {
        return Grade.class;
    }

    @Override
    public String getFindAllQuery() {
        return FIND_ALL_GRADES_QUERY;
    }
}

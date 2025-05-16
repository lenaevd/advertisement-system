package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Grade;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GradeDao extends Dao<Grade> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradeDao.class);

    private static final String FIND_ALL_GRADES_QUERY = "SELECT grade FROM Grade grade";
    private static final String FIND_GRADE_BY_USERS_QUERY = "SELECT g FROM Grade g " +
            "WHERE g.seller.id = :sellerId AND g.customer.id = :customerId";
    private static final String FIND_GRADES_BY_SELLER_ID_QUERY = "SELECT g FROM Grade g WHERE g.seller.id = :sellerId";

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

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    public Optional<Grade> findByUsers(int sellerId, int customerId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Grade> query = session.createQuery(FIND_GRADE_BY_USERS_QUERY, getEntityClass());
        query.setParameter("sellerId", sellerId);
        query.setParameter("customerId", customerId);
        query.setMaxResults(1);
        return Optional.ofNullable(query.uniqueResult());
    }

    public List<Grade> findBySellerId(int sellerId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_GRADES_BY_SELLER_ID_QUERY, getEntityClass())
                .setParameter("sellerId", sellerId)
                .getResultList();
    }
}

package com.lenaevd.advertisements.dao.impl;

import com.lenaevd.advertisements.dao.UserDao;
import com.lenaevd.advertisements.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDaoImpl extends HibernateDao<User> implements UserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String FIND_ALL_USERS_QUERY = "SELECT user FROM User user";
    private static final String FIND_USER_BY_NAME_QUERY = "SELECT u FROM User u WHERE u.username = :username";
    private static final String FIND_USER_BY_EMAIL_QUERY = "SELECT u FROM User u WHERE u.email = :email";

    protected UserDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public String getFindAllQuery() {
        return FIND_ALL_USERS_QUERY;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(FIND_USER_BY_NAME_QUERY, getEntityClass());
        query.setParameter("username", username);
        query.setMaxResults(1);
        return Optional.ofNullable(query.uniqueResult());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(FIND_USER_BY_EMAIL_QUERY, getEntityClass());
        query.setParameter("email", email);
        query.setMaxResults(1);
        return Optional.ofNullable(query.uniqueResult());
    }
}

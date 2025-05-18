package com.lenaevd.advertisements.dao.impl;

import com.lenaevd.advertisements.config.LoggerMessages;
import com.lenaevd.advertisements.dao.Dao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class HibernateDao<T> implements Dao<T> {

    protected final Logger logger;
    protected final SessionFactory sessionFactory;

    protected HibernateDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.logger = getLogger();
    }

    protected <R> R executeInTryCatch(Function<Session, R> function) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return function.apply(session);
        } catch (Exception e) {
            logger.error(LoggerMessages.DAO_ERROR, e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(T object) {
        executeInTryCatch(session -> {
            session.persist(object);
            logger.debug(LoggerMessages.OBJECT_SAVED, object);
            return null;
        });
    }

    @Override
    public void update(T object) {
        executeInTryCatch(session -> {
            session.merge(object);
            logger.debug(LoggerMessages.OBJECT_UPDATED, object);
            return null;
        });
    }

    @Override
    public Optional<T> findById(int id) {
        return executeInTryCatch(session -> {
            T object = session.find(getEntityClass(), id);
            return Optional.ofNullable(object);
        });
    }

    @Override
    public List<T> findAll() {
        return executeInTryCatch(session -> session.createQuery(getFindAllQuery(), getEntityClass()).getResultList());
    }

    @Override
    public void delete(T object) {
        executeInTryCatch(session -> {
            session.remove(object);
            logger.debug(LoggerMessages.OBJECT_DELETED, object);
            return null;
        });
    }

    public abstract Class<T> getEntityClass();

    public abstract String getFindAllQuery();

    public abstract Logger getLogger();
}

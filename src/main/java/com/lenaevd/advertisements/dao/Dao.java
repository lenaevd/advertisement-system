package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.config.LoggerMessages;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public abstract class Dao<T> {

    protected final Logger logger;
    protected final SessionFactory sessionFactory;

    protected Dao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.logger = getLogger();
    }

    public void save(T object) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.persist(object);
            logger.debug(LoggerMessages.OBJECT_SAVED, object);
        } catch (Exception e) {
            logger.error(LoggerMessages.DAO_ERROR, e.getMessage());
        }
    }

    public void update(T object) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.merge(object);
            logger.debug(LoggerMessages.OBJECT_UPDATED, object);
        } catch (Exception e) {
            logger.error(LoggerMessages.DAO_ERROR, e.getMessage());
        }
    }

    public Optional<T> findById(int id) {
        try {
            Session session = sessionFactory.getCurrentSession();
            T object = session.find(getEntityClass(), id);
            return Optional.ofNullable(object);
        } catch (Exception e) {
            logger.error(LoggerMessages.DAO_ERROR, e.getMessage());
            return Optional.empty();
        }
    }

    public List<T> findAll() {
        try {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery(getFindAllQuery(), getEntityClass()).getResultList();
        } catch (Exception e) {
            logger.error(LoggerMessages.DAO_ERROR, e.getMessage());
            return List.of();
        }
    }

    public void deleteById(int id) {
        try {
            Session session = sessionFactory.getCurrentSession();
            T object = session.find(getEntityClass(), id);
            if (object != null) {
                session.remove(object);
                logger.debug(LoggerMessages.OBJECT_DELETED, object);
            }
        } catch (Exception e) {
            logger.error(LoggerMessages.DAO_ERROR, e.getMessage());
        }
    }

    public abstract Class<T> getEntityClass();

    public abstract String getFindAllQuery();

    public abstract Logger getLogger();
}

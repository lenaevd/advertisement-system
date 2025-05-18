package com.lenaevd.advertisements.dao.impl;

import com.lenaevd.advertisements.dao.MessageDao;
import com.lenaevd.advertisements.model.Message;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class MessageDaoImpl extends HibernateDao<Message> implements MessageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDaoImpl.class);

    private static final String FIND_ALL_MESSAGES_QUERY = "SELECT m FROM Message m";

    protected MessageDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<Message> getEntityClass() {
        return Message.class;
    }

    @Override
    public String getFindAllQuery() {
        return FIND_ALL_MESSAGES_QUERY;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}

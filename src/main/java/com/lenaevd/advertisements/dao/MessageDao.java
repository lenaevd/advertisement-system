package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Message;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageDao extends Dao<Message> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDao.class);

    private static final String FIND_ALL_MESSAGES_QUERY = "SELECT m FROM Message m";
    private static final String FIND_MESSAGES_BY_CHAT_QUERY = "SELECT m FROM Message m WHERE m.chat.id = :chatId";

    protected MessageDao(SessionFactory sessionFactory) {
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

    public List<Message> findByChatId(int chatId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_MESSAGES_BY_CHAT_QUERY, getEntityClass())
                .setParameter("chatId", chatId)
                .getResultList();
    }
}

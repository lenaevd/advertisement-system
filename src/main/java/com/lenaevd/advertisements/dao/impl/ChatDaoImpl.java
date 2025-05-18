package com.lenaevd.advertisements.dao.impl;

import com.lenaevd.advertisements.dao.ChatDao;
import com.lenaevd.advertisements.model.Chat;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatDaoImpl extends HibernateDao<Chat> implements ChatDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatDaoImpl.class);

    private static final String FIND_ALL_CHATS_QUERY = "SELECT chat FROM Chat chat";
    private static final String FIND_USERS_CHATS_QUERY = "SELECT chat FROM Chat chat " +
            "WHERE chat.advertisement.seller.id = :userId OR chat.customer.id = :userId";
    private static final String FIND_CHAT_BY_AD_AND_CUSTOMER_QUERY = "SELECT chat FROM Chat chat " +
            "WHERE chat.advertisement.id = :adId AND chat.customer.id = :userId";

    protected ChatDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<Chat> getEntityClass() {
        return Chat.class;
    }

    @Override
    public String getFindAllQuery() {
        return FIND_ALL_CHATS_QUERY;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public List<Chat> findChatsByUserId(int userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_USERS_CHATS_QUERY, getEntityClass())
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Optional<Chat> findChatByAdvertisementIdAndCustomerId(int adId, int customerId) {
        Session session = sessionFactory.getCurrentSession();
        Chat chat = session.createQuery(FIND_CHAT_BY_AD_AND_CUSTOMER_QUERY, getEntityClass())
                .setParameter("adId", adId)
                .setParameter("userId", customerId)
                .setMaxResults(1)
                .uniqueResult();
        return Optional.ofNullable(chat);
    }
}

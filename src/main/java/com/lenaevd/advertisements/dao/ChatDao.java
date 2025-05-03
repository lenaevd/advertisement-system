package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Chat;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ChatDao extends Dao<Chat> {
    public static final String FIND_ALL_CHATS_QUERY = "SELECT chat FROM Chat chat";

    protected ChatDao(SessionFactory sessionFactory) {
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
}

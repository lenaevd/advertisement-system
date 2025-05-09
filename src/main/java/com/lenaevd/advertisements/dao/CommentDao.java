package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Comment;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDao extends Dao<Comment> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDao.class);

    private static final String FIND_ALL_COMMENTS_QUERY = "SELECT comment FROM Comment comment";

    protected CommentDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<Comment> getEntityClass() {
        return Comment.class;
    }

    @Override
    public String getFindAllQuery() {
        return FIND_ALL_COMMENTS_QUERY;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}

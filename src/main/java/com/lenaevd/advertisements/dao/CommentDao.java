package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Comment;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDao extends Dao<Comment> {
    public static final String FIND_ALL_COMMENTS_QUERY = "SELECT comment FROM Comment comment";

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
}

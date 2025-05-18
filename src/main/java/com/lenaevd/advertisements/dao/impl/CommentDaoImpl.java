package com.lenaevd.advertisements.dao.impl;

import com.lenaevd.advertisements.dao.CommentDao;
import com.lenaevd.advertisements.model.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDaoImpl extends HibernateDao<Comment> implements CommentDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDaoImpl.class);

    private static final String FIND_ALL_COMMENTS_QUERY = "SELECT comment FROM Comment comment";
    private static final String FIND_BY_AD_ID_QUERY = "SELECT com FROM Comment com WHERE com.advertisement.id = :adId";

    protected CommentDaoImpl(SessionFactory sessionFactory) {
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

    @Override
    public List<Comment> findByAdvertisementId(int adId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_BY_AD_ID_QUERY, getEntityClass())
                .setParameter("adId", adId)
                .getResultList();
    }
}

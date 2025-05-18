package com.lenaevd.advertisements.dao.impl;

import com.lenaevd.advertisements.dao.AdvertisementDao;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.model.AdvertisementType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdvertisementDaoImpl extends HibernateDao<Advertisement> implements AdvertisementDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementDaoImpl.class);

    private static final String FIND_ALL_ADS_QUERY = "SELECT ad FROM Advertisement ad";
    private static final String FIND_ADS_BY_SELLER_ID_QUERY = "SELECT ad FROM Advertisement ad WHERE ad.seller.id = :sellerId";
    private static final String FIND_ADS_BY_SELLER_ID_AND_STATUS_QUERY = "SELECT ad FROM Advertisement ad " +
            "WHERE ad.seller.id = :sellerId AND ad.status = :status";
    private static final String FIND_ADS_BY_TYPES_AND_STATUS_QUERY = "SELECT ad FROM Advertisement ad " +
            "WHERE ad.type IN (:types) AND ad.status = :status";
    private static final String FIND_ADS_BY_STATUS_QUERY = "SELECT ad FROM Advertisement ad WHERE ad.status = :status";
    private static final String FIND_BY_KEYWORD_IN_TITLE_QUERY = "SELECT ad FROM Advertisement ad " +
            "WHERE LOWER(ad.title) LIKE :keyword AND ad.status = :status";

    protected AdvertisementDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<Advertisement> getEntityClass() {
        return Advertisement.class;
    }

    @Override
    public String getFindAllQuery() {
        return FIND_ALL_ADS_QUERY;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public List<Advertisement> findAllAdsBySellerId(int sellerId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_ADS_BY_SELLER_ID_QUERY, getEntityClass())
                .setParameter("sellerId", sellerId)
                .getResultList();
    }

    @Override
    public List<Advertisement> findAdsBySellerIdAndStatus(int sellerId, AdvertisementStatus status) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_ADS_BY_SELLER_ID_AND_STATUS_QUERY, getEntityClass())
                .setParameter("sellerId", sellerId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Advertisement> findAdsByTypesAndStatus(List<AdvertisementType> types, AdvertisementStatus status) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_ADS_BY_TYPES_AND_STATUS_QUERY, getEntityClass())
                .setParameter("types", types)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Advertisement> findAdsByStatus(AdvertisementStatus status) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_ADS_BY_STATUS_QUERY, getEntityClass())
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Advertisement> findAdsByKeywordInTitle(String keyword, AdvertisementStatus status) {
        Session session = sessionFactory.getCurrentSession();
        Query<Advertisement> query = session.createQuery(FIND_BY_KEYWORD_IN_TITLE_QUERY, getEntityClass());
        query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
        query.setParameter("status", status);
        return query.getResultList();
    }
}

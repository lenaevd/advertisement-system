package com.lenaevd.advertisements.dao;

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
public class AdvertisementDao extends Dao<Advertisement> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementDao.class);

    private static final String FIND_ALL_ADS_QUERY = "SELECT ad FROM Advertisement ad";
    private static final String FIND_ADS_BY_SELLER_ID_QUERY = "SELECT ad FROM Advertisement ad WHERE ad.seller.id = :sellerId";
    private static final String FIND_ADS_BY_SELLER_ID_AND_STATUS_QUERY = "SELECT ad FROM Advertisement ad " +
            "WHERE ad.seller.id = :sellerId AND ad.status = :status";
    private static final String FIND_ADS_BY_TYPES_AND_STATUS_QUERY = "SELECT ad FROM Advertisement ad " +
            "WHERE ad.type IN (:types) AND ad.status = :status";
    private static final String FIND_ADS_BY_STATUS_QUERY = "SELECT ad FROM Advertisement ad WHERE ad.status = :status";
    private static final String FIND_BY_KEYWORD_IN_TITLE_QUERY = "SELECT ad FROM Advertisement ad " +
            "WHERE LOWER(ad.title) LIKE :keyword AND ad.status = :status";

    protected AdvertisementDao(SessionFactory sessionFactory) {
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

    public List<Advertisement> findAllAdsBySellerId(int sellerId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_ADS_BY_SELLER_ID_QUERY, getEntityClass())
                .setParameter("sellerId", sellerId)
                .getResultList();
    }

    public List<Advertisement> findAdsBySellerIdAndStatus(int sellerId, AdvertisementStatus status) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_ADS_BY_SELLER_ID_AND_STATUS_QUERY, getEntityClass())
                .setParameter("sellerId", sellerId)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Advertisement> findAdsByTypesAndStatus(List<AdvertisementType> types, AdvertisementStatus status) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_ADS_BY_TYPES_AND_STATUS_QUERY, getEntityClass())
                .setParameter("types", types)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Advertisement> findAdsByStatus(AdvertisementStatus status) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_ADS_BY_STATUS_QUERY, getEntityClass())
                .setParameter("status", status)
                .getResultList();
    }

    public List<Advertisement> findAdsByKeywordInTitle(String keyword, AdvertisementStatus status) {
        Session session = sessionFactory.getCurrentSession();
        Query<Advertisement> query = session.createQuery(FIND_BY_KEYWORD_IN_TITLE_QUERY, getEntityClass());
        query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
        query.setParameter("status", status);
        return query.getResultList();
    }
}

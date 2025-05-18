package com.lenaevd.advertisements.dao.impl;

import com.lenaevd.advertisements.dao.SaleDao;
import com.lenaevd.advertisements.model.Sale;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaleDaoImpl extends HibernateDao<Sale> implements SaleDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleDaoImpl.class);

    private static final String FIND_ALL_SALES_QUERY = "SELECT sale FROM Sale sale";
    private static final String FIND_SOLD_ITEMS_QUERY = "SELECT s FROM Sale s WHERE s.advertisement.seller.id = :sellerId";
    private static final String FIND_PURCHASED_ITEMS_QUERY = "SELECT s FROM Sale s WHERE s.customer.id = :customerId";

    protected SaleDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<Sale> getEntityClass() {
        return Sale.class;
    }

    @Override
    public String getFindAllQuery() {
        return FIND_ALL_SALES_QUERY;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public List<Sale> findSoldItems(int sellerId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_SOLD_ITEMS_QUERY, getEntityClass())
                .setParameter("sellerId", sellerId)
                .getResultList();
    }

    @Override
    public List<Sale> findPurchasedItems(int customerId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(FIND_PURCHASED_ITEMS_QUERY, getEntityClass())
                .setParameter("customerId", customerId)
                .getResultList();
    }
}

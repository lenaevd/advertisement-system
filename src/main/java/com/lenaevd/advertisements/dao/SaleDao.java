package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Sale;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaleDao extends Dao<Sale> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleDao.class);

    private static final String FIND_ALL_SALES_QUERY = "SELECT sale FROM Sale sale";
    private static final String FIND_SOLD_ITEMS_QUERY = "SELECT s FROM Sale s WHERE s.advertisement.seller.id = :sellerId";
    private static final String FIND_PURCHASED_ITEMS_QUERY = "SELECT s FROM Sale s WHERE s.customer.id = :customerId";

    protected SaleDao(SessionFactory sessionFactory) {
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

    public List<Sale> findSoldItems(int sellerId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(FIND_SOLD_ITEMS_QUERY, getEntityClass())
                    .setParameter("sellerId", sellerId)
                    .getResultList();
        }
    }

    public List<Sale> findPurchasedItems(int customerId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(FIND_PURCHASED_ITEMS_QUERY, getEntityClass())
                    .setParameter("customerId", customerId)
                    .getResultList();
        }
    }
}

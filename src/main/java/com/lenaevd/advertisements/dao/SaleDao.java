package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Sale;
import org.hibernate.SessionFactory;

public class SaleDao extends Dao<Sale> {
    public static final String FIND_ALL_SALES_QUERY = "SELECT sale FROM Sale sale";

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
}

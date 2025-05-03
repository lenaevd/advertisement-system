package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Advertisement;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class AdvertisementDao extends Dao<Advertisement> {

    public static final String FIND_ALL_ADS_QUERY = "SELECT ad FROM Advertisement ad";

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
}

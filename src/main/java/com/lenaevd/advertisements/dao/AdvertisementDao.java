package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.model.AdvertisementType;

import java.util.List;

public interface AdvertisementDao extends Dao<Advertisement> {
    List<Advertisement> findAllAdsBySellerId(int sellerId);

    List<Advertisement> findAdsBySellerIdAndStatus(int sellerId, AdvertisementStatus status);

    List<Advertisement> findAdsByTypesAndStatus(List<AdvertisementType> types, AdvertisementStatus status);

    List<Advertisement> findAdsByStatus(AdvertisementStatus status);

    List<Advertisement> findAdsByKeywordInTitle(String keyword, AdvertisementStatus status);
}

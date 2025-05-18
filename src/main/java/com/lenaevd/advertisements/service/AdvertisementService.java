package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dto.ad.CreateAdRequest;
import com.lenaevd.advertisements.dto.ad.PremiumRequest;
import com.lenaevd.advertisements.dto.ad.UpdateAdRequest;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.model.AdvertisementType;

import java.security.Principal;
import java.util.List;

public interface AdvertisementService {
    List<Advertisement> getAdvertisementsFeed();

    List<Advertisement> getAdvertisementsByTypesAndStatus(List<AdvertisementType> types, AdvertisementStatus status);

    List<Advertisement> searchAdvertisementsByKeywordAndStatus(String keyword, AdvertisementStatus status);

    Advertisement getAdvertisementById(int id);

    void createAdvertisement(CreateAdRequest request, Principal principal);

    void archiveAdvertisement(int id, Principal principal);

    void unarchiveAdvertisement(int id, Principal principal);

    void completeAdvertisement(int id, Principal sellerPrincipal, int customerId);

    Advertisement updateAdvertisement(UpdateAdRequest request, Principal principal);

    void makeAdvertisementPremium(PremiumRequest request, Principal principal);

    List<Advertisement> getAdvertisementBySellerId(int sellerId, Principal principal);

    List<Advertisement> getAll();

    void deleteAdvertisement(int adId);
}

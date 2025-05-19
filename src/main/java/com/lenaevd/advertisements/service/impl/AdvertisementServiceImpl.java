package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.config.LoggerMessages;
import com.lenaevd.advertisements.dao.AdvertisementDao;
import com.lenaevd.advertisements.dao.impl.SaleDaoImpl;
import com.lenaevd.advertisements.dto.ad.CreateAdRequest;
import com.lenaevd.advertisements.dto.ad.PremiumRequest;
import com.lenaevd.advertisements.dto.ad.UpdateAdRequest;
import com.lenaevd.advertisements.exception.ActionIsImpossibleException;
import com.lenaevd.advertisements.exception.NoRightsException;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.model.AdvertisementType;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.Sale;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.AdvertisementService;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementServiceImpl.class);

    private final AdvertisementDao advertisementDao;
    private final UserService userService;
    private final SaleDaoImpl saleDao;

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getAdvertisementsFeed() {
        LOGGER.debug("Executing getAdvertisementsFeed");
        List<Advertisement> ads = advertisementDao.findAdsByStatus(AdvertisementStatus.ACTIVE);

        ads.sort((ad1, ad2) -> {
            if (ad1.isPremiumActive() != ad2.isPremiumActive()) {
                return Boolean.compare(ad2.isPremiumActive(), ad1.isPremiumActive());
            }
            return Double.compare(ad2.getSeller().getRating(), ad1.getSeller().getRating());
        });
        return ads;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getAdvertisementsByTypesAndStatus(List<AdvertisementType> types, AdvertisementStatus status) {
        LOGGER.debug("Executing search by types={}", types);
        return advertisementDao.findAdsByTypesAndStatus(types, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> searchAdvertisementsByKeywordAndStatus(String keyword, AdvertisementStatus status) {
        LOGGER.debug("Executing search by keyword={}", keyword);
        return advertisementDao.findAdsByKeywordInTitle(keyword, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Advertisement getAdvertisementById(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "getById", EntityName.ADVERTISEMENT, id);
        return advertisementDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.ADVERTISEMENT));
    }

    @Override
    @Transactional
    public void createAdvertisement(CreateAdRequest request, Principal principal) {
        User seller = userService.getUserFromPrincipal(principal);
        Advertisement ad = new Advertisement(request.title(), request.content(), request.price(), seller, request.type());
        advertisementDao.save(ad);
        LOGGER.info("Created {}", EntityName.ADVERTISEMENT);
    }

    private Advertisement getAdIfExistsAndUserIsAuthor(int adId, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Optional<Advertisement> adOptional = advertisementDao.findById(adId);
        if (adOptional.isPresent()) {
            Advertisement ad = adOptional.get();
            if (user.equals(ad.getSeller())) {
                return ad;
            } else {
                throw new NoRightsException("User and ad's author doesn't match");
            }
        } else {
            throw new ObjectNotFoundException(adId, EntityName.ADVERTISEMENT);
        }
    }

    @Override
    @Transactional
    public void archiveAdvertisement(int id, Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "archiveAdvertisement", EntityName.ADVERTISEMENT, id);
        Advertisement ad = getAdIfExistsAndUserIsAuthor(id, principal);
        if (ad.getStatus() == AdvertisementStatus.ACTIVE) {
            ad.setStatus(AdvertisementStatus.ARCHIVED);
            advertisementDao.update(ad);
            LOGGER.info("Advertisement with id={} archived", ad.getId());
        } else {
            throw new ActionIsImpossibleException("Only active ads can be archived");
        }
    }

    @Override
    @Transactional
    public void unarchiveAdvertisement(int id, Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "unarchiveAdvertisement", EntityName.ADVERTISEMENT, id);
        Advertisement ad = getAdIfExistsAndUserIsAuthor(id, principal);
        if (ad.getStatus() == AdvertisementStatus.ARCHIVED) {
            ad.setStatus(AdvertisementStatus.ACTIVE);
            advertisementDao.update(ad);
            LOGGER.info("Advertisement with id={} unarchived", ad.getId());
        } else {
            throw new ActionIsImpossibleException("Only archived ads can turn active");
        }
    }

    @Override
    @Transactional
    public void completeAdvertisement(int id, Principal sellerPrincipal, int customerId) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "completeAdvertisement", EntityName.ADVERTISEMENT, id);
        Advertisement ad = getAdIfExistsAndUserIsAuthor(id, sellerPrincipal);
        User customer = userService.getUserById(customerId);

        if (ad.getStatus() == AdvertisementStatus.ACTIVE) {
            ad.setStatus(AdvertisementStatus.COMPLETED);
            Sale sale = new Sale(ad, customer);
            ad.setSale(sale);
            saleDao.save(sale);
            advertisementDao.update(ad);
            LOGGER.info("Advertisement with id={} completed", ad.getId());
        } else {
            throw new ActionIsImpossibleException("Only active ads can be completed");
        }
    }

    @Override
    @Transactional
    public Advertisement updateAdvertisement(UpdateAdRequest request, Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "updateAdvertisement", EntityName.ADVERTISEMENT, request.id());
        Advertisement ad = getAdIfExistsAndUserIsAuthor(request.id(), principal);
        boolean updated = false;
        if (isNotNullAndNotBlank(request.title())) {
            ad.setTitle(request.title());
            updated = true;
        }
        if (isNotNullAndNotBlank(request.content())) {
            ad.setContent(request.content());
            updated = true;
        }
        if (request.price() != null) {
            ad.setPrice(request.price());
            updated = true;
        }
        if (request.type() != null) {
            ad.setType(request.type());
            updated = true;
        }
        if (updated) {
            ad.setUpdatedAt(LocalDateTime.now());
            advertisementDao.update(ad);
            LOGGER.info(LoggerMessages.UPDATE_COMPLETED, EntityName.ADVERTISEMENT, ad.getId());
        }
        return ad;
    }

    private boolean isNotNullAndNotBlank(String s) {
        if (s == null) {
            return false;
        } else {
            return !s.isBlank();
        }
    }

    @Override
    @Transactional
    public void makeAdvertisementPremium(PremiumRequest request, Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "makePremium", EntityName.ADVERTISEMENT, request.id());
        Advertisement ad = getAdIfExistsAndUserIsAuthor(request.id(), principal);
        if (ad.getStatus() == AdvertisementStatus.ACTIVE) {
            ad.setPremiumExpiryDate(LocalDateTime.now().plus(request.plan().getPeriod()));
            advertisementDao.update(ad);
            LOGGER.info("Advertisement id={} is premium now", ad.getId());
        } else {
            throw new ActionIsImpossibleException("Only active ads can get premium");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getAdvertisementsBySellerId(int sellerId, Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "getAdvertisementsBySellerId", EntityName.USER, sellerId);
        User adsAuthor = userService.getUserById(sellerId);
        User requester = userService.getUserFromPrincipal(principal);

        if (adsAuthor.equals(requester)) {
            LOGGER.info("Returned all ads to author");
            return advertisementDao.findAllAdsBySellerId(sellerId);
        } else {
            LOGGER.info("Returned active ads to user");
            return advertisementDao.findAdsBySellerIdAndStatus(sellerId, AdvertisementStatus.ACTIVE);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getAll() {
        LOGGER.debug(LoggerMessages.ALL_OBJECTS_REQUESTED);
        return advertisementDao.findAll();
    }

    @Override
    @Transactional
    public void deleteAdvertisement(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "deleteAdvertisement", EntityName.ADVERTISEMENT, id);
        Advertisement ad = getAdvertisementById(id);
        advertisementDao.delete(ad);
        LOGGER.info(LoggerMessages.DELETE_COMPLETED, EntityName.ADVERTISEMENT, id);
    }
}

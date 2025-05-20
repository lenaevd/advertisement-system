package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dao.AdvertisementDao;
import com.lenaevd.advertisements.dao.impl.SaleDaoImpl;
import com.lenaevd.advertisements.dto.ad.CreateAdRequest;
import com.lenaevd.advertisements.dto.ad.PremiumRequest;
import com.lenaevd.advertisements.dto.ad.UpdateAdRequest;
import com.lenaevd.advertisements.exception.NoRightsException;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.model.AdvertisementType;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.PremiumPlan;
import com.lenaevd.advertisements.model.Sale;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.impl.AdvertisementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {
    @Mock
    private AdvertisementDao advertisementDao;
    @Mock
    private UserService userService;
    @Mock
    private SaleDaoImpl saleDao;

    @InjectMocks
    private AdvertisementServiceImpl adService;

    @Test
    void getAdvertisementsFeed() {
        //GIVEN
        Advertisement ad1 = new Advertisement();
        ad1.setPremiumExpiryDate(null);
        User user1 = new User();
        user1.setRating(4);
        ad1.setSeller(user1);

        Advertisement ad2 = new Advertisement();
        ad2.setPremiumExpiryDate(LocalDateTime.now().plusDays(10));
        User user2 = new User();
        user2.setRating(4);
        ad2.setSeller(user2);

        Advertisement ad3 = new Advertisement();
        ad3.setPremiumExpiryDate(null);
        User user3 = new User();
        user3.setRating(4.5F);
        ad3.setSeller(user3);

        List<Advertisement> adsUnsorted = Arrays.asList(ad1, ad2, ad3);
        List<Advertisement> adsExpected = Arrays.asList(ad2, ad3, ad1);
        when(advertisementDao.findAdsByStatus(AdvertisementStatus.ACTIVE)).thenReturn(adsUnsorted);

        //WHEN
        List<Advertisement> result = adService.getAdvertisementsFeed();

        //THEN
        assertEquals(adsExpected, result);
    }

    @Test
    void getAdvertisementById() {
        //GIVEN
        int id = 10;
        Advertisement ad = new Advertisement();
        ad.setId(id);
        when(advertisementDao.findById(id)).thenReturn(Optional.of(ad));

        //WHEN
        Advertisement result = adService.getAdvertisementById(id);

        //THEN
        assertEquals(ad, result);
    }

    @Test
    void getAdvertisementByIdAndNotFound() {
        //GIVEN
        int id = 10;
        when(advertisementDao.findById(id)).thenReturn(Optional.empty());

        //WHEN | THEN
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> adService.getAdvertisementById(id));
        assertEquals(new ObjectNotFoundException(id, EntityName.ADVERTISEMENT).getMessage(), exception.getMessage());
    }

    @Test
    void createAdvertisement() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User seller = new User();
        CreateAdRequest request = new CreateAdRequest("title", "content", 100, AdvertisementType.TRANSPORT);
        when(userService.getUserFromPrincipal(principal)).thenReturn(seller);

        //WHEN
        adService.createAdvertisement(request, principal);

        //THEN
        verify(advertisementDao).save(any(Advertisement.class));
    }

    @Test
    void archiveAdvertisement() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User seller = new User();
        int userId = 5;
        seller.setId(userId);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdvertisementStatus.ACTIVE);
        ad.setSeller(seller);
        int adId = 20;
        ad.setId(adId);

        when(userService.getUserFromPrincipal(principal)).thenReturn(seller);
        when(advertisementDao.findById(adId)).thenReturn(Optional.of(ad));

        //WHEN
        adService.archiveAdvertisement(adId, principal);

        //THEN
        verify(advertisementDao).update(ad);
        assertEquals(AdvertisementStatus.ARCHIVED, ad.getStatus());
    }

    @Test
    void unarchiveAdvertisement() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User seller = new User();
        int userId = 5;
        seller.setId(userId);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdvertisementStatus.ARCHIVED);
        ad.setSeller(seller);
        int adId = 20;
        ad.setId(adId);

        when(userService.getUserFromPrincipal(principal)).thenReturn(seller);
        when(advertisementDao.findById(adId)).thenReturn(Optional.of(ad));

        //WHEN
        adService.unarchiveAdvertisement(adId, principal);

        //THEN
        verify(advertisementDao).update(ad);
        assertEquals(AdvertisementStatus.ACTIVE, ad.getStatus());
    }

    @Test
    void completeAdvertisement() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User seller = new User();
        int userId = 5;
        seller.setId(userId);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdvertisementStatus.ACTIVE);
        ad.setSeller(seller);
        int adId = 20;
        ad.setId(adId);

        User customer = new User();
        int customerId = 11;
        customer.setId(customerId);

        when(userService.getUserById(customerId)).thenReturn(customer);
        when(userService.getUserFromPrincipal(principal)).thenReturn(seller);
        when(advertisementDao.findById(adId)).thenReturn(Optional.of(ad));

        //WHEN
        adService.completeAdvertisement(adId, principal, customerId);

        //THEN
        verify(saleDao).save(any(Sale.class));
        verify(advertisementDao).update(ad);
        assertEquals(AdvertisementStatus.COMPLETED, ad.getStatus());
    }

    @Test
    void completeAdvertisementAndUserNotAuthorThrowsException() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User user = new User();
        int userId = 5;
        user.setId(userId);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdvertisementStatus.ACTIVE);
        int adId = 20;
        ad.setSeller(new User());
        ad.setId(adId);

        when(userService.getUserFromPrincipal(principal)).thenReturn(user);
        when(advertisementDao.findById(adId)).thenReturn(Optional.of(ad));

        //WHEN | THEN
        assertThrows(NoRightsException.class, () -> adService.completeAdvertisement(adId, principal, 1));
        verify(saleDao, never()).save(any(Sale.class));
        verify(advertisementDao, never()).update(ad);
    }

    @Test
    void completeAdvertisementAndObjectNotFoundThrowsException() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User user = new User();
        int userId = 5;
        user.setId(userId);
        int adId = 20;

        when(userService.getUserFromPrincipal(principal)).thenReturn(user);
        when(advertisementDao.findById(adId)).thenReturn(Optional.empty());

        //WHEN | THEN
        assertThrows(ObjectNotFoundException.class, () -> adService.completeAdvertisement(adId, principal, 1));
        verify(saleDao, never()).save(any(Sale.class));
        verify(advertisementDao, never()).update(any(Advertisement.class));
    }

    @Test
    void updateAdvertisement() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User seller = new User();
        int userId = 5;
        seller.setId(userId);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdvertisementStatus.ACTIVE);
        ad.setTitle("title");
        ad.setContent("content");
        ad.setPrice(10);
        ad.setSeller(seller);
        int adId = 20;
        ad.setId(adId);

        UpdateAdRequest request = new UpdateAdRequest(adId, null, "newContent", 100, null);
        when(userService.getUserFromPrincipal(principal)).thenReturn(seller);
        when(advertisementDao.findById(adId)).thenReturn(Optional.of(ad));

        //WHEN
        adService.updateAdvertisement(request, principal);

        //THEN
        verify(advertisementDao).update(ad);
        assertEquals("title", ad.getTitle());
        assertEquals(request.content(), ad.getContent());
        assertEquals(100, ad.getPrice());
    }

    @Test
    void makeAdvertisementPremium() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User seller = new User();
        int userId = 5;
        seller.setId(userId);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdvertisementStatus.ACTIVE);
        ad.setSeller(seller);
        int adId = 20;
        ad.setId(adId);

        PremiumRequest request = new PremiumRequest(adId, PremiumPlan.DAY);
        when(userService.getUserFromPrincipal(principal)).thenReturn(seller);
        when(advertisementDao.findById(adId)).thenReturn(Optional.of(ad));

        //WHEN
        adService.makeAdvertisementPremium(request, principal);

        //THEN
        verify(advertisementDao).update(ad);
        assertNotEquals(null, ad.getPremiumExpiryDate());
    }

    @Test
    void getAdvertisementsBySellerIdWhenRequesterIsAuthor() {
        //GIVEN
        int sellerId = 10;
        Principal principal = mock(Principal.class);
        User seller = new User();
        seller.setId(sellerId);
        List<Advertisement> ads = List.of(new Advertisement(), new Advertisement());

        when(userService.getUserById(sellerId)).thenReturn(seller);
        when(userService.getUserFromPrincipal(principal)).thenReturn(seller);
        when(advertisementDao.findAllAdsBySellerId(sellerId)).thenReturn(ads);

        //WHEN
        List<Advertisement> result = adService.getAdvertisementsBySellerId(sellerId, principal);

        //THEN
        assertEquals(ads, result);
        verify(advertisementDao).findAllAdsBySellerId(sellerId);
        verify(advertisementDao, never()).findAdsBySellerIdAndStatus(any(Integer.class), any(AdvertisementStatus.class));
    }

    @Test
    void getAdvertisementsBySellerIdWhenRequesterIsNotAuthor() {
        //GIVEN
        Principal principal = mock(Principal.class);
        User requester = new User();
        requester.setId(20);

        int sellerId = 10;
        User seller = new User();
        seller.setId(sellerId);
        List<Advertisement> ads = List.of(new Advertisement(), new Advertisement());

        when(userService.getUserById(sellerId)).thenReturn(seller);
        when(userService.getUserFromPrincipal(principal)).thenReturn(requester);
        when(advertisementDao.findAdsBySellerIdAndStatus(sellerId, AdvertisementStatus.ACTIVE)).thenReturn(ads);

        //WHEN
        List<Advertisement> result = adService.getAdvertisementsBySellerId(sellerId, principal);

        //THEN
        assertEquals(ads, result);
        verify(advertisementDao).findAdsBySellerIdAndStatus(sellerId, AdvertisementStatus.ACTIVE);
        verify(advertisementDao, never()).findAllAdsBySellerId(any(Integer.class));
    }
}

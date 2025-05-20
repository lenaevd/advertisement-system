package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.ad.AdvertisementDto;
import com.lenaevd.advertisements.dto.ad.CreateAdRequest;
import com.lenaevd.advertisements.dto.ad.FilterAdByTypeRequest;
import com.lenaevd.advertisements.dto.ad.PremiumRequest;
import com.lenaevd.advertisements.dto.ad.UpdateAdRequest;
import com.lenaevd.advertisements.mapper.AdvertisementMapper;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.model.AdvertisementType;
import com.lenaevd.advertisements.model.PremiumPlan;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.AdvertisementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdvertisementControllerTest {
    @Mock
    private AdvertisementService adService;
    @Mock
    private AdvertisementMapper mapper;

    @InjectMocks
    private AdvertisementController advertisementController;

    private Principal principal;
    private Advertisement ad;
    private AdvertisementDto adDto;
    private List<Advertisement> ads;
    private List<AdvertisementDto> adDtos;
    private int id = 1;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        ad = new Advertisement("title", "content", 100, new User(), AdvertisementType.ANIMALS);
        adDto = new AdvertisementDto(1, "title", "content", 100, 1,
                AdvertisementType.ANIMALS, AdvertisementStatus.ACTIVE, LocalDateTime.now(), null, null);
        ads = List.of(ad);
        adDtos = List.of(adDto);
    }

    @Test
    void getAllAds() {
        //GIVEN
        when(adService.getAll()).thenReturn(ads);
        when(mapper.adsToAdDtos(ads)).thenReturn(adDtos);

        //WHEN
        ResponseEntity<List<AdvertisementDto>> response = advertisementController.getAllAds();

        //THEN
        verify(adService).getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDtos, response.getBody());
    }

    @Test
    void getFeed() {
        //GIVEN
        when(adService.getAdvertisementsFeed()).thenReturn(ads);
        when(mapper.adsToAdDtos(ads)).thenReturn(adDtos);

        //WHEN
        ResponseEntity<List<AdvertisementDto>> response = advertisementController.getFeed();

        //THEN
        verify(adService).getAdvertisementsFeed();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDtos, response.getBody());
    }

    @Test
    void getAdsByTypes() {
        //GIVEN
        FilterAdByTypeRequest request = new FilterAdByTypeRequest(List.of(AdvertisementType.ANIMALS));
        when(adService.getAdvertisementsByTypesAndStatus(request.types(), AdvertisementStatus.ACTIVE))
                .thenReturn(ads);
        when(mapper.adsToAdDtos(ads)).thenReturn(adDtos);

        //WHEN
        ResponseEntity<List<AdvertisementDto>> response = advertisementController.getAdsByTypes(request);

        //THEN
        verify(adService).getAdvertisementsByTypesAndStatus(request.types(), AdvertisementStatus.ACTIVE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDtos, response.getBody());
    }

    @Test
    void searchAdvertisements() {
        //GIVEN
        String keyword = "test";
        when(adService.searchAdvertisementsByKeywordAndStatus(keyword, AdvertisementStatus.ACTIVE))
                .thenReturn(ads);
        when(mapper.adsToAdDtos(ads)).thenReturn(adDtos);

        //WHEN
        ResponseEntity<List<AdvertisementDto>> response = advertisementController.searchAdvertisements(keyword);

        //THEN
        verify(adService).searchAdvertisementsByKeywordAndStatus(keyword, AdvertisementStatus.ACTIVE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDtos, response.getBody());
    }

    @Test
    void getAdvertisementById() {
        //GIVEN
        when(adService.getAdvertisementById(id)).thenReturn(ad);
        when(mapper.adToAdDto(ad)).thenReturn(adDto);

        //WHEN
        ResponseEntity<AdvertisementDto> response = advertisementController.getAdvertisementById(id);

        //THEN
        verify(adService).getAdvertisementById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDto, response.getBody());
    }

    @Test
    void createAdvertisement() {
        //GIVEN
        CreateAdRequest request = new CreateAdRequest("title", "content", 100, AdvertisementType.ANIMALS);

        //WHEN
        ResponseEntity<Void> response = advertisementController.createAdvertisement(request, principal);

        //THEN
        verify(adService).createAdvertisement(request, principal);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void deleteAdvertisement() {
        //WHEN
        ResponseEntity<Void> response = advertisementController.deleteAdvertisement(id);

        //THEN
        verify(adService).deleteAdvertisement(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void archiveAdvertisement() {
        //WHEN
        ResponseEntity<Void> response = advertisementController.archiveAdvertisement(id, principal);

        //THEN
        verify(adService).archiveAdvertisement(id, principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void unarchiveAdvertisement() {
        //WHEN
        ResponseEntity<Void> response = advertisementController.unarchiveAdvertisement(id, principal);

        //THEN
        verify(adService).unarchiveAdvertisement(id, principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAdvertisementInfo() {
        //GIVEN
        UpdateAdRequest request = new UpdateAdRequest(1, "new title", "new content", 150,
                AdvertisementType.ELECTRONICS);
        when(adService.updateAdvertisement(request, principal)).thenReturn(ad);
        when(mapper.adToAdDto(ad)).thenReturn(adDto);

        //WHEN
        ResponseEntity<AdvertisementDto> response = advertisementController.updateAdvertisementInfo(request, principal);

        //THEN
        verify(adService).updateAdvertisement(request, principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDto, response.getBody());
    }

    @Test
    void payForPremium() {
        //GIVEN
        PremiumRequest request = new PremiumRequest(1, PremiumPlan.DAY);

        //WHEN
        ResponseEntity<Void> response = advertisementController.payForPremium(request, principal);

        //THEN
        verify(adService).makeAdvertisementPremium(request, principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

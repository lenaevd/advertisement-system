package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.ad.AdvertisementDto;
import com.lenaevd.advertisements.dto.ad.CreateAdRequest;
import com.lenaevd.advertisements.dto.ad.FilterAdByTypeRequest;
import com.lenaevd.advertisements.dto.ad.PremiumRequest;
import com.lenaevd.advertisements.dto.ad.UpdateAdRequest;
import com.lenaevd.advertisements.mapper.AdvertisementMapper;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.service.AdvertisementService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/ads")
public class AdvertisementController {
    private final AdvertisementService adService;
    private final AdvertisementMapper mapper;

    public AdvertisementController(AdvertisementService adService, AdvertisementMapper mapper) {
        this.adService = adService;
        this.mapper = mapper;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AdvertisementDto>> getAllAds() {
        return ResponseEntity.ok(mapper.adsToAdDtos(adService.getAll()));
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<AdvertisementDto>> getFeed() {
        return ResponseEntity.ok(mapper.adsToAdDtos(adService.getAdvertisementsFeed()));
    }

    @GetMapping("/filtered")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<AdvertisementDto>> getAdsByType(@RequestBody @Validated FilterAdByTypeRequest request) {
        List<Advertisement> ads = adService.getAdvertisementsByTypesAndStatus(request.types(), AdvertisementStatus.ACTIVE);
        return ResponseEntity.ok(mapper.adsToAdDtos(ads));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<AdvertisementDto>> searchAdvertisements(@RequestParam @NotBlank String keyword) {
        List<Advertisement> ads = adService.searchAdvertisementsByKeywordAndStatus(keyword, AdvertisementStatus.ACTIVE);
        return ResponseEntity.ok(mapper.adsToAdDtos(ads));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<AdvertisementDto> getAdvertisementById(@PathVariable @NotNull int id) {
        return ResponseEntity.ok(mapper.adToAdDto(adService.getAdvertisementById(id)));
    }

    @GetMapping("/personal")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<AdvertisementDto>> getAdvertisementsBySellerId(@RequestParam @NotNull int userId,
                                                                              Principal principal) {
        return ResponseEntity.ok(mapper.adsToAdDtos(adService.getAdvertisementBySellerId(userId, principal)));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> createAdvertisement(@RequestBody @Validated CreateAdRequest request,
                                                    Principal principal) {
        adService.createAdvertisement(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAdvertisement(@RequestParam @NotNull int id) {
        adService.deleteAdvertisement(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/archive")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> archiveAdvertisement(@RequestParam @NotNull Integer id, Principal principal) {
        adService.archiveAdvertisement(id, principal);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/unarchive")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> unarchiveAdvertisement(@RequestParam @NotNull int id, Principal principal) {
        adService.unarchiveAdvertisement(id, principal);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/complete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> completeAdvertisement(@RequestParam @NotNull int id, @RequestParam @NotNull int customerId,
                                                      Principal principal) {
        adService.completeAdvertisement(id, principal, customerId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<AdvertisementDto> updateAdvertisementInfo(@RequestBody @Validated UpdateAdRequest request,
                                                                    Principal principal) {
        Advertisement ad = adService.updateAdvertisement(request, principal);
        return ResponseEntity.ok(mapper.adToAdDto(ad));
    }

    @PatchMapping("/premium")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> payForPremium(@RequestBody @Validated PremiumRequest request, Principal principal) {
        adService.makeAdvertisementPremium(request, principal);
        return ResponseEntity.ok().build();
    }
}

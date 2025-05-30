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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "Advertisements", description = "endpoints to work with ads: create, update, search and so on")
public class AdvertisementController {
    private final AdvertisementService adService;
    private final AdvertisementMapper mapper;

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all ads", description = "No matter what status, allowed only for admin")
    public ResponseEntity<List<AdvertisementDto>> getAllAds() {
        return ResponseEntity.ok(mapper.adsToAdDtos(adService.getAll()));
    }

    @GetMapping("/feed")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get ads feed",
            description = "Returns active ads sorted by users ratings and considering premium")
    public ResponseEntity<List<AdvertisementDto>> getFeed() {
        return ResponseEntity.ok(mapper.adsToAdDtos(adService.getAdvertisementsFeed()));
    }

    @PostMapping("/filtered")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Find ads by type")
    public ResponseEntity<List<AdvertisementDto>> getAdsByTypes(@RequestBody @Validated FilterAdByTypeRequest request) {
        List<Advertisement> ads = adService.getAdvertisementsByTypesAndStatus(request.types(), AdvertisementStatus.ACTIVE);
        return ResponseEntity.ok(mapper.adsToAdDtos(ads));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Search ad by keyword in title")
    public ResponseEntity<List<AdvertisementDto>> searchAdvertisements(@RequestParam @NotBlank String keyword) {
        List<Advertisement> ads = adService.searchAdvertisementsByKeywordAndStatus(keyword, AdvertisementStatus.ACTIVE);
        return ResponseEntity.ok(mapper.adsToAdDtos(ads));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get ad", description = "Allowed only for admin")
    public ResponseEntity<AdvertisementDto> getAdvertisementById(@PathVariable int id) {
        return ResponseEntity.ok(mapper.adToAdDto(adService.getAdvertisementById(id)));
    }

    @GetMapping("/personal")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get user's ads",
            description = "If user requests other's ads, returns only active ads, else returns ads in all statuses")
    public ResponseEntity<List<AdvertisementDto>> getAdvertisementsBySellerId(@RequestParam @NotNull Integer userId,
                                                                              Principal principal) {
        return ResponseEntity.ok(mapper.adsToAdDtos(adService.getAdvertisementsBySellerId(userId, principal)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Create ad")
    public ResponseEntity<Void> createAdvertisement(@RequestBody @Validated CreateAdRequest request,
                                                    Principal principal) {
        adService.createAdvertisement(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete ad", description = "Allowed only for admin")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable int id) {
        adService.deleteAdvertisement(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/archive")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Archive ad")
    public ResponseEntity<Void> archiveAdvertisement(@RequestParam @NotNull Integer id, Principal principal) {
        adService.archiveAdvertisement(id, principal);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/unarchive")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Unarchive ad")
    public ResponseEntity<Void> unarchiveAdvertisement(@RequestParam @NotNull Integer id, Principal principal) {
        adService.unarchiveAdvertisement(id, principal);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/complete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Complete ad", description = "Creates new sale, so customer can grade seller")
    public ResponseEntity<Void> completeAdvertisement(@RequestParam @NotNull Integer id, @RequestParam @NotNull Integer customerId,
                                                      Principal principal) {
        adService.completeAdvertisement(id, principal, customerId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Update ad", description = "Title, content, price or type can be updated")
    public ResponseEntity<AdvertisementDto> updateAdvertisementInfo(@RequestBody @Validated UpdateAdRequest request,
                                                                    Principal principal) {
        Advertisement ad = adService.updateAdvertisement(request, principal);
        return ResponseEntity.ok(mapper.adToAdDto(ad));
    }

    @PatchMapping("/premium")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get premium", description = "Enables premium plan for the ad")
    public ResponseEntity<Void> payForPremium(@RequestBody @Validated PremiumRequest request, Principal principal) {
        adService.makeAdvertisementPremium(request, principal);
        return ResponseEntity.ok().build();
    }
}

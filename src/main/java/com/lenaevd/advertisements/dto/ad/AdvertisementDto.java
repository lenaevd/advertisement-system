package com.lenaevd.advertisements.dto.ad;

import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.model.AdvertisementType;

import java.time.LocalDateTime;

public record AdvertisementDto(
        int id,
        String title,
        String content,
        int price,
        int sellerId,
        AdvertisementType type,
        AdvertisementStatus status,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt,
        LocalDateTime premiumExpiryDate
) {
}

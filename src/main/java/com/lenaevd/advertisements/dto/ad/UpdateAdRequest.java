package com.lenaevd.advertisements.dto.ad;

import com.lenaevd.advertisements.model.AdvertisementType;
import jakarta.validation.constraints.NotNull;

public record UpdateAdRequest(
        @NotNull
        int id,
        String title,
        String content,
        Integer price,
        AdvertisementType type
) {
}

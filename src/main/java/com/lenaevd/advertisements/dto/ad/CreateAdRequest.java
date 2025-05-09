package com.lenaevd.advertisements.dto.ad;

import com.lenaevd.advertisements.model.AdvertisementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAdRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String title,
        @NotBlank
        String content,
        @NotNull
        int price,
        @NotNull
        AdvertisementType type
) {
}

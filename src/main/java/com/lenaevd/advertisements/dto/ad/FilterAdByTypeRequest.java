package com.lenaevd.advertisements.dto.ad;

import com.lenaevd.advertisements.model.AdvertisementType;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record FilterAdByTypeRequest(
        @NotEmpty
        List<AdvertisementType> types
) {
}

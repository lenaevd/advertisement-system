package com.lenaevd.advertisements.dto.ad;

import com.lenaevd.advertisements.model.PremiumPlan;
import jakarta.validation.constraints.NotNull;

public record PremiumRequest(
        @NotNull
        int id,
        @NotNull
        PremiumPlan plan
) {
}

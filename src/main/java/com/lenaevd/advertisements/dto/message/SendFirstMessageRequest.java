package com.lenaevd.advertisements.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendFirstMessageRequest(
        @NotNull
        int advertisementId,
        @NotBlank
        String content
) {
}

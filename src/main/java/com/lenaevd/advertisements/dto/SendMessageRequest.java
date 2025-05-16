package com.lenaevd.advertisements.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
        @NotNull
        int chatId,
        @NotBlank
        String content
) {
}

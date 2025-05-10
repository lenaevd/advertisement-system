package com.lenaevd.advertisements.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeaveCommentRequest(
        @NotNull
        int adId,
        @NotBlank
        String content
) {
}

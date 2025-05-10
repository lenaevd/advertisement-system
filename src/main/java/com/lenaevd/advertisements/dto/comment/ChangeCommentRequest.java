package com.lenaevd.advertisements.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeCommentRequest(
        @NotNull
        int id,
        @NotBlank
        String content
) {
}

package com.lenaevd.advertisements.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}

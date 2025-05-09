package com.lenaevd.advertisements.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank
        @Size(min = 8, max = 25)
        String newPassword
) {
}

package com.lenaevd.advertisements.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUsernameRequest(
        @NotBlank
        @Size(min = 3, max = 25)
        String newUsername
) {
}

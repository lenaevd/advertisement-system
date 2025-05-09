package com.lenaevd.advertisements.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(
        @Email
        @NotBlank
        String newEmail
) {
}

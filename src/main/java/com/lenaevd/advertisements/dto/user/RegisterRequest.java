package com.lenaevd.advertisements.dto.user;

import com.lenaevd.advertisements.config.AdvertisementsConstants;
import com.lenaevd.advertisements.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 25)
        String username,
        @Email
        @NotBlank
        String email,
        @NotBlank
        @Pattern(regexp = AdvertisementsConstants.REGEX_FOR_PASSWORD,
                message = AdvertisementsConstants.VALID_MESSAGE_FOR_PASSWORD)
        String password,
        @NotNull
        Role role) {
}

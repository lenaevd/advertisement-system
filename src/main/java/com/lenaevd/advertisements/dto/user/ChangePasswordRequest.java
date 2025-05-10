package com.lenaevd.advertisements.dto.user;

import com.lenaevd.advertisements.config.AdvertisementsConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotBlank
        @Pattern(regexp = AdvertisementsConstants.REGEX_FOR_PASSWORD,
        message = AdvertisementsConstants.VALID_MESSAGE_FOR_PASSWORD)
        String newPassword
) {
}

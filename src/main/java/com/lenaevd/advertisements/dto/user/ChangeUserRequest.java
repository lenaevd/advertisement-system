package com.lenaevd.advertisements.dto.user;

import com.lenaevd.advertisements.config.AdvertisementsConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangeUserRequest(
        @Size(min = 3, max = 25)
        String username,
        @Pattern(regexp = AdvertisementsConstants.REGEX_FOR_PASSWORD,
                message = AdvertisementsConstants.VALID_MESSAGE_FOR_PASSWORD)
        String password,
        @Email
        String email
) {
}

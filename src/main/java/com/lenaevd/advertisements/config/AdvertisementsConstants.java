package com.lenaevd.advertisements.config;

public class AdvertisementsConstants {
    public static final String REGEX_FOR_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,25}$";
    public static final String VALID_MESSAGE_FOR_PASSWORD = "password must contain at least " +
            "one capital, small letter, digit, special symbol, no tabs and spaces, " +
            "and the length must be between 8 and 25 characters";
}

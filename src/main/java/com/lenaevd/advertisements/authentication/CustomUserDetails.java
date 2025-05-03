package com.lenaevd.advertisements.authentication;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {
    String getEmail();
}

package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.signing.key}")
    private String signingKey;
    @Value("${jwt.key.expiration}")
    private Long jwtExpiration;

    private SecretKey key;

    private SecretKey getSecretKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    public String generateToken(User user) {
        String token = Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .subject(user.getUsername())
                .claim("email", user.getEmail())
                .signWith(getSecretKey())
                .compact();
        LOGGER.debug("Generated new token");
        return token;
    }

    public boolean isValid(String token, CustomUserDetails userDetails) {
        Claims claims = getClaims(token);
        String username = claims.getSubject();
        Date expiration = claims.getExpiration();
        String email = (String) claims.get("email");

        boolean isValid = expiration.after(new Date(System.currentTimeMillis()))
                && username.equals(userDetails.getUsername())
                && email.equals(userDetails.getEmail());
        if (isValid) {
            LOGGER.debug("Token is valid");
        } else {
            LOGGER.debug("Invalid token");
        }
        return isValid;
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

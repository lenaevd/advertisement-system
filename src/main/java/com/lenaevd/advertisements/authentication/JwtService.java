package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

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
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .subject(user.getUsername())
                .claim("email", user.getEmail())
                .signWith(getSecretKey())
                .compact();
    }

    public boolean isValid(String token, CustomUserDetails userDetails) {
        Claims claims = getClaims(token);
        String username = claims.getSubject();
        Date expiration = claims.getExpiration();
        String email = (String) claims.get("email");

        return expiration.after(new Date(System.currentTimeMillis()))
                && username.equals(userDetails.getUsername())
                && email.equals(userDetails.getEmail());
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

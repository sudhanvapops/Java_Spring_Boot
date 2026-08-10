package com.sudhanva.library_management_v2.security;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;


    // generate secret key
    public SecretKey getSigningKey() {
        // Base64 string → bytes
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        // Step 2 — bytes → SecretKey
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Build the Access Token
    private String buildToken(
        Map<String, Object> claims,
        UserPrincipal user,
        Instant tokenExpiresAt
    ) {

        

        return Jwts.builder()

                // Payolad Part all are Claims

                // These become part of the JWT payload.
                .claims(claims)
                // Usually stores the username or user id.
                // Subject is the email since CustomUserDetailsService looks users up by email.
                .subject(user.getEmail())
                // Time when this JWT was created.
                .issuedAt(new Date())
                // Token expiry time.
                // Here: 1hour minutes from now.
                .expiration(Date.from(tokenExpiresAt))

                // Signature Part

                .signWith(getSigningKey())
                // Build the JWT and convert it into a compact String.
                .compact();

    }


    // Generate Access Token
    public String generateAccessToken(UserPrincipal user) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("role", user.getRole());
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());

        Instant tokenExpiresAt =
            Instant.now().plusMillis(
                jwtConfig.getAccessTokenExpiration()
            );

        return buildToken(claims, user,tokenExpiresAt);

    }

    // Generate Refresh Token
    // jti (jwt identifier) is passed in so the caller can persist the
    // matching RefreshToken record without this service knowing about the DB.
    public String generateRefrehToken(UserPrincipal userDetails, String jti) {

        Map<String,Object> claims = new HashMap<>();
        claims.put("jti", jti);
        claims.put("type", "refresh");

        Instant tokenExpiresAt =
            Instant.now().plusMillis(
                jwtConfig.getRefreshTokenExpiration()
            );

        return buildToken(
            claims,
            userDetails,
            tokenExpiresAt
        );
    }

    // Get All Claims and verify
    public Claims getAllClaims(String token) {

        // It also Handles Expiry Check
        return Jwts.parser()
                // Verify the signature
                .verifyWith(getSigningKey())
                // if true build jwt
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public String getUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public String getEmail(String token) {
        return getAllClaims(token).get("email", String.class);
    }

    public Date getExpiration(String token) {
        return getAllClaims(token).getExpiration();
    }

    public Date getIssuedAt(String token) {
        return getAllClaims(token).getIssuedAt();
    }

}

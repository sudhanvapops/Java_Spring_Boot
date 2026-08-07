package com.sudhanva.library_management_v2.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.config.JwtConfig;
import com.sudhanva.library_management_v2.security.UserPrincipal;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;


    // generate secreate key
    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // Generate Token
    public String generateToken(UserPrincipal user) {

        long expiresAt = System.currentTimeMillis() + jwtConfig.getExpiration();

        // -----------------------------------------------------------------
        // Claims = Extra information stored inside the JWT payload.
        //
        // Some claims are predefined:
        // sub -> Subject (usually username)
        // exp -> Expiration time
        // iat -> Issued at
        //
        // You can also add custom claims like:
        // role
        // email
        // userId
        // -----------------------------------------------------------------
        Map<String, Object> claims = new HashMap<>();

        

        claims.put("role", user.getRole());
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                // These become part of the JWT payload.
                .claims(claims)
                // Usually stores the username or user id.
                .subject(user.getUsername())
                // Time when this JWT was created.
                .issuedAt(new Date())
                // Token expiry time.
                // Here: 1hour minutes from now.
                .expiration(new Date(expiresAt))

                .signWith(getSigningKey())
                // Build the JWT and convert it into a compact String.
                .compact();

    }

}

package com.sudhanva.library_management_v2.security;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.RefreshToken;
import com.sudhanva.library_management_v2.Model.User;
import com.sudhanva.library_management_v2.repo.RefreshTokenRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    public RefreshToken save(String jti, User user, Instant expiresAt) {

        RefreshToken refreshTokenEntity = RefreshToken
            .builder()
            .jti(jti)
            .revoked(false)
            .createdAt(Instant.now())
            .expiresAt(expiresAt)
            .user(user)
            .build();

        return refreshTokenRepo.save(refreshTokenEntity);
    }
}

package com.sudhanva.library_management_v2.Model.Dto.Auth.login;

import com.sudhanva.library_management_v2.enums.User.UserRoles;

import lombok.Builder;

@Builder
public record LoginResponseDto(
    String accessToken,
    String accessTokenType,
    
    String refreshToken,
    String refreshTokenType,
    
    Long userId,
    String email,
    UserRoles role
) {
    
}

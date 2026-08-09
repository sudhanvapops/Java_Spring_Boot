package com.sudhanva.library_management_v2.Model.Dto.Auth.refresh;

import com.sudhanva.library_management_v2.enums.User.UserRoles;

import lombok.Builder;

@Builder
public record RefreshResponseDto(
    String accessToken,
    String accessTokenType,
    
    Long userId,
    String email,
    UserRoles role
) {
    
}

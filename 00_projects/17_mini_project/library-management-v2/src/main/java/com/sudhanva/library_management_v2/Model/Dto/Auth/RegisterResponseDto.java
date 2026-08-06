package com.sudhanva.library_management_v2.Model.Dto.Auth;

import com.sudhanva.library_management_v2.enums.User.UserRoles;

import lombok.Builder;

@Builder
public record RegisterResponseDto(
    String username,
    String email,
    UserRoles role
) {
    
}

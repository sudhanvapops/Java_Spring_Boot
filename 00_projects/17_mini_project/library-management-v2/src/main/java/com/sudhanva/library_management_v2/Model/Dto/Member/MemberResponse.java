package com.sudhanva.library_management_v2.Model.Dto.Member;

import com.sudhanva.library_management_v2.enums.User.UserRoles;

import lombok.Builder;

@Builder
public record MemberResponse(
    Long id,
    String name,
    String email,
    Integer age,
    Boolean isActive,
    UserRoles role
) {
    
}

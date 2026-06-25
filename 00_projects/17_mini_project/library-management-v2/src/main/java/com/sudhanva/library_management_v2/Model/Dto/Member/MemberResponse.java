package com.sudhanva.library_management_v2.Model.Dto.Member;

import lombok.Builder;

@Builder
public record MemberResponse(
    Long id,
    String name,
    String email,
    Integer age
) {
    
}

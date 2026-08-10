package com.sudhanva.library_management_v2.Model.Dto.Auth.logout;

import lombok.Builder;

@Builder
public record LogoutResponseDto(
    String user
) {

}
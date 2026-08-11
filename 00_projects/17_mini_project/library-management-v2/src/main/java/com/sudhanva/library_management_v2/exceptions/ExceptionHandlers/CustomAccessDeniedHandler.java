package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.Error.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

// Invoked by Spring Security when an authenticated user's role fails a
// @PreAuthorize check (e.g. a MEMBER hitting a @StaffOnly endpoint). Mirrors
// CustomAuthenticationEntryPoint's JSON shape so 401s and 403s look the same.
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
            .success(false)
            .errorCode(ErrorCode.FORBIDDEN)
            .message("You do not have permission to access this resource")
            .timestamp(LocalDateTime.now())
            .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}

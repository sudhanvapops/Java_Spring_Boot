package com.sudhanva.library_management_v2.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.Error.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

// Invoked by Spring Security whenever a request reaches a protected endpoint
// without a valid Authentication in the SecurityContext (missing, expired,
// malformed or otherwise rejected JWT). JwtFilter stores the specific reason
// as a request attribute; we fall back to a generic message when it's absent
// (e.g. no token was sent at all).
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String AUTH_ERROR_ATTRIBUTE = "auth_error_message";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {

        String message = (String) request.getAttribute(AUTH_ERROR_ATTRIBUTE);
        if (message == null) {
            message = "Full authentication is required to access this resource";
        }

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
            .success(false)
            .errorCode(ErrorCode.UNAUTHORIZED)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}

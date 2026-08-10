package com.sudhanva.library_management_v2.security.Auth;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Auth.login.LoginRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.login.LoginResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.logout.LogoutResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.refresh.RefreshResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.refresh.RefreshServiceResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.RegisterRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.RegisterResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.security.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints for account registration and login (no bearer token required)")
public class AuthController {

    final AuthService authService;


    // Helper Method
    
    private RefreshResponseDto mapToRefreshResponseDto(RefreshServiceResponseDto result) {
         return RefreshResponseDto
            .builder()
            .email(result.email())
            .role(result.role())
            .accessTokenType("Bearer")
            .accessToken(result.accessToken())
            .userId(result.userId())
            .build();
    }



    // Public signup (MEMBER only)
    @Operation(summary = "Register a new librarian account", description = "Public signup; accounts are always created with the LIBRARIAN role")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account created",
        content = @Content(schema = @Schema(implementation = RegisterResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "password and confirmPassword do not match",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "A user with the same email or username already exists",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(
       @Valid @RequestBody RegisterRequestDto request
    ){

        ApiResponse<RegisterResponseDto> response =
            authService.register(request);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Log in", description = "Authenticates by email and password and returns a Bearer JWT to use with the Authorize button for protected endpoints")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
        content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No user exists with the given email, or the password is incorrect "
        + "(message is intentionally generic to avoid revealing which one was wrong)",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
        @Valid @RequestBody LoginRequestDto request,
        HttpServletResponse response
    ){

        ApiResponse<LoginResponseDto> result = authService.login(request);

        ResponseCookie refreshCookie = ResponseCookie
            .from("refreshToken",result.data().refreshToken())
            .httpOnly(true)
            // can send it http and https
            // production make it true
            .secure(false)
            // cross site
            .sameSite("strict")
            .path("/api/auth")
            .maxAge(Duration.ofDays(7))
            .build();
        
        response.addHeader(
            HttpHeaders.SET_COOKIE,
            refreshCookie.toString()
        );

        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Refresh the access token", description = "Reads the httpOnly refreshToken cookie set by /login, validates it against the stored record, "
        + "and issues a new short-lived access token together with a rotated refresh token (the old refresh token is revoked).")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Access token refreshed",
        content = @Content(schema = @Schema(implementation = RefreshServiceResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "refreshToken cookie is missing",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token is malformed/expired (as a JWT), not of type refresh, "
        + "has no matching record, has been revoked, has expired (per the stored record), or its subject no longer matches the associated user",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponseDto>> refresh(
        @Parameter(description = "httpOnly refreshToken cookie set by /login", required = true)
        @CookieValue(value = "refreshToken") String refreshToken,
        HttpServletResponse response
    ){
        ApiResponse<RefreshServiceResponseDto> result = authService.refreshAccessToken(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie
            .from("refreshToken",result.data().newRefrshToken())
            .httpOnly(true)
            // can send it http and https
            // production make it true
            .secure(false)
            // cross site
            .sameSite("strict")
            .path("/api/auth")
            .maxAge(Duration.ofDays(7))
            .build();

        response.addHeader(
            HttpHeaders.SET_COOKIE,
            refreshCookie.toString()
        );


        return ResponseEntity.ok(
            new ApiResponse<>(
                result.success(),
                result.message(),
                mapToRefreshResponseDto(result.data())
            )
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponseDto>> logout(
        @CookieValue(value = "refreshToken") String refreshToken,
        HttpServletResponse response
    ){
        
        ApiResponse<LogoutResponseDto> result = 
            authService.logout(refreshToken);

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok(result);

    }


    // TODO: Delete USER

}

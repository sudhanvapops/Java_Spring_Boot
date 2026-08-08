package com.sudhanva.library_management_v2.controller.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Auth.login.LoginRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.login.LoginResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.RegisterRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.RegisterResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.Service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints for account registration and login (no bearer token required)")
public class AuthController {

    final AuthService authService;


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
        @Valid @RequestBody LoginRequestDto request
    ){
        return ResponseEntity.ok(authService.login(request));
    }


    // TODO: Logout Route
    // TODO: Refresh Route
    // TODO: Delete USER

}

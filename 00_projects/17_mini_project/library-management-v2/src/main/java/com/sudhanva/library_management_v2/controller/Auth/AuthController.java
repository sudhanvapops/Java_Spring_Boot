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
import com.sudhanva.library_management_v2.Service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    final AuthService authService;


    // Public signup (MEMBER only)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(
       @Valid @RequestBody RegisterRequestDto request
    ){

        ApiResponse<RegisterResponseDto> response = 
            authService.register(request);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
        @Valid @RequestBody LoginRequestDto request
    ){

        return ResponseEntity.ok(authService.login(request));
    }


    // TODO: Logout Route
    // TODO: Refresh Route

}

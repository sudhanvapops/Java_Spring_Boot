package com.sudhanva.library_management_v2.Service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.User;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Auth.login.LoginRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.login.LoginResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.RegisterRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.RegisterResponseDto;
import com.sudhanva.library_management_v2.enums.User.UserRoles;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UsernameAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.PasswordAndConfirmPasswordDoesntMatchException;
import com.sudhanva.library_management_v2.repo.UserRepo;
import com.sudhanva.library_management_v2.security.UserPrincipal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    private RegisterResponseDto mapToRegisterResponseDto(User user){
        return RegisterResponseDto
            .builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }

    private User getUser(RegisterRequestDto request) {
        User user = User.builder()
            .email(request.email())
            .username(request.username())
            .isActive(true)
            .role(UserRoles.LIBRARIAN)
            .build();
        return user;
    }

    private LoginResponseDto mapToLoginResposeDto(
        String token,
        UserPrincipal principal
    ){
        return LoginResponseDto
            .builder()
            .email(principal.getEmail())
            .role(principal.getRole())
            .tokenType("Bearer")
            .accessToken(token)
            .userId(principal.getId())
            .build();
    }


    @Transactional
    public ApiResponse<RegisterResponseDto> register(
        RegisterRequestDto request
    ) {

        // 1. Validate password confirmation
        if(!request.password().equals(request.confirmPassword())){
            throw new PasswordAndConfirmPasswordDoesntMatchException();
        }


        // 2. Check user already exists
        User existingUser = userRepo.existsByUsername(request.username())
            .orElse(null);

        if (existingUser != null){
            throw new UsernameAlreadyExistsException(request.username());
        }


        // 3. Check email uniqueness
        User existingUserByEmail = userRepo.existsByEmail(request.email())
            .orElse(null);

        if(existingUserByEmail != null){
            throw new MemberEmailAlreadyExistsException(request.email());
        }

        // 4. Create User
        User user = getUser(request);
        
        // hash Password
        String hashedPassword = passwordEncoder.encode(request.password());
        user.setPassword(hashedPassword);

        // 5. Save
        User savedUser = userRepo.save(user);

        // 6. Return response
        return new ApiResponse<>(
            true,
            "Librarain Account created Successfully",
            mapToRegisterResponseDto(savedUser)
        );
    }


    @Transactional
    public ApiResponse<LoginResponseDto> login(LoginRequestDto request) {


        // Extraxt the token -> UserNameandpasswordToken
        // Put to Authentication Manger.authentcaite()
        // it authentcate in DaoProvider
        // DaoProvider 
        // load user
        // passwrod match
        // generate token
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(), 
                request.password()
            )
        );

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        // Generate JWT
        String jwtToken = jwtService.generateToken(userDetails);

        return new ApiResponse<>(
            true,
            "User logged In",
            mapToLoginResposeDto(jwtToken,userDetails)
        );
       
    }




}

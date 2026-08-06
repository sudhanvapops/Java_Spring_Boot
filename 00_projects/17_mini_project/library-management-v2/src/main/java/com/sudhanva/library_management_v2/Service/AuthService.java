package com.sudhanva.library_management_v2.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.User;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Auth.RegisterRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.RegisterResponseDto;
import com.sudhanva.library_management_v2.enums.User.UserRoles;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UserEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.PasswordAndConfirmPasswordDoesntMatchException;
import com.sudhanva.library_management_v2.repo.UserRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    private RegisterResponseDto mapToRegisterResponseDto(User user){
        return RegisterResponseDto
            .builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
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
        User existingUser = userRepo.findByUsername(request.username())
            .orElse(null);

        if (existingUser == null){
            throw new UserEmailAlreadyExistsException(request.username());
        }


        // 3. Check email uniqueness
        userRepo.findByEmail(request.email())
            .orElseThrow(
                () -> new MemberEmailAlreadyExistsException(request.email())
            );


        // 4. Create User
        User user = User.builder()
            .email(request.email())
            .username(request.username())
            .isActive(true)
            .role(UserRoles.MEMBER)
            .build();
        
        // hash Password
        String hashedPassword = passwordEncoder.encode(request.password());
        user.setPassword(hashedPassword);

        // 5. Save
        User savedUser = userRepo.save(user);

        // 6. Return response
        return new ApiResponse<>(
            true,
            "",
            mapToRegisterResponseDto(savedUser)
        );
    }
    
}

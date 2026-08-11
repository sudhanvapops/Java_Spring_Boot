package com.sudhanva.library_management_v2.security;


import com.sudhanva.library_management_v2.repo.RefreshTokenRepo;
import java.time.Instant;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.RefreshToken;
import com.sudhanva.library_management_v2.Model.User;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Auth.login.LoginRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.login.LoginResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.logout.LogoutResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.refresh.RefreshServiceResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.RegisterRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.RegisterResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Auth.register.StaffRegisterRequestDto;
import com.sudhanva.library_management_v2.enums.User.UserRoles;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.NoRefreshTokenRecordExists;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.NotRefreshTokenException;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.TokenExpiredException;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.TokenRevokedException;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.TokenSubjectMismatchException;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.InvalidStaffRoleException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UsernameAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.PasswordAndConfirmPasswordDoesntMatchException;
import com.sudhanva.library_management_v2.repo.UserRepo;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {


    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private static final String REFRESH_TOKEN_TYPE = "refresh";


    private RegisterResponseDto mapToRegisterResponseDto(User user){
        return RegisterResponseDto
            .builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }

    // Public self-signup always creates a MEMBER account - it's the only
    // role that isn't a privilege escalation for an anonymous caller.
    // ADMIN/LIBRARIAN accounts are provisioned via registerStaff() instead.
    private User getUser(RegisterRequestDto request) {
        User user = User.builder()
            .email(request.email())
            .username(request.username())
            .isActive(true)
            .role(UserRoles.MEMBER)
            .build();
        return user;
    }

    private User getStaffUser(StaffRegisterRequestDto request) {
        User user = User.builder()
            .email(request.email())
            .username(request.username())
            .isActive(true)
            .role(request.role())
            .build();
        return user;
    }

    private LoginResponseDto mapToLoginResposeDto(
        String accessToken,
        UserPrincipal principal,
        String refreshToken
    ){
        return LoginResponseDto
            .builder()
            .email(principal.getEmail())
            .role(principal.getRole())
            .accessTokenType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .refreshTokenType("Cookie")
            .userId(principal.getId())
            .build();
    }

    private RefreshServiceResponseDto mapToRefreshServiceResponseDto(
        String accessToken,
        String newRefrshToken,
        UserPrincipal principal
    ){
        return RefreshServiceResponseDto
            .builder()
            .email(principal.getEmail())
            .role(principal.getRole())
            .accessTokenType("Bearer")
            .accessToken(accessToken)
            .newRefrshToken(newRefrshToken)
            .userId(principal.getId())
            .build();
    }

    private LogoutResponseDto mapToLogoutResponseDto(Claims claims){
        return LogoutResponseDto.builder().user(claims.getSubject()).build();
    }

    // Shared by refreshAccessToken() and logout(): confirms the JWT is actually a
    // refresh token and resolves its persisted RefreshToken record.
    private RefreshToken findValidatedRefreshToken(Claims claims){
        String tokenType = claims.get("type", String.class);
        if (!REFRESH_TOKEN_TYPE.equals(tokenType)){
            throw new NotRefreshTokenException();
        }

        String jti = claims.get("jti", String.class);
        return refreshTokenRepo
            .findByJti(jti)
            .orElseThrow(() -> new NoRefreshTokenRecordExists(jti));
    }


    // Service Methods

    @Transactional
    public ApiResponse<RegisterResponseDto> register(
        RegisterRequestDto request
    ) {

        // 1. Validate password confirmation
        if(!request.password().equals(request.confirmPassword())){
            throw new PasswordAndConfirmPasswordDoesntMatchException();
        }


        // 2. Check user already exists
        if (userRepo.existsByUsername(request.username())){
            throw new UsernameAlreadyExistsException(request.username());
        }


        // 3. Check email uniqueness
        if (userRepo.existsByEmail(request.email())){
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
            "Member Account created Successfully",
            mapToRegisterResponseDto(savedUser)
        );
    }


    // Admin-only: provisions LIBRARIAN or ADMIN accounts. Kept separate from
    // register() so the public signup path can never be used to self-assign
    // a privileged role.
    @Transactional
    public ApiResponse<RegisterResponseDto> registerStaff(
        StaffRegisterRequestDto request
    ) {

        // 1. Validate requested role is actually a staff role
        if (request.role() != UserRoles.ADMIN && request.role() != UserRoles.LIBRARIAN) {
            throw new InvalidStaffRoleException(request.role());
        }

        // 2. Validate password confirmation
        if(!request.password().equals(request.confirmPassword())){
            throw new PasswordAndConfirmPasswordDoesntMatchException();
        }

        // 3. Check user already exists
        if (userRepo.existsByUsername(request.username())){
            throw new UsernameAlreadyExistsException(request.username());
        }

        // 4. Check email uniqueness
        if (userRepo.existsByEmail(request.email())){
            throw new MemberEmailAlreadyExistsException(request.email());
        }

        // 5. Create User
        User user = getStaffUser(request);

        // hash Password
        String hashedPassword = passwordEncoder.encode(request.password());
        user.setPassword(hashedPassword);

        // 6. Save
        User savedUser = userRepo.save(user);

        // 7. Return response
        return new ApiResponse<>(
            true,
            request.role() + " Account created Successfully",
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

        // After Authentication
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        // Generate JWT
        String jwtToken = jwtService.generateAccessToken(userDetails);

        // Generate Refresh Token (token creation only)
        String jti = UUID.randomUUID().toString();
        String refreshToken = jwtService.generateRefrehToken(userDetails, jti);

        // Persist Refresh Token 
        Instant refreshTokenExpiresAt = jwtService.getExpiration(refreshToken).toInstant();
        refreshTokenService.save(jti, userDetails.getUser(), refreshTokenExpiresAt);


        return new ApiResponse<>(
            true,
            "User logged In",
            mapToLoginResposeDto(jwtToken,userDetails,refreshToken)
        );
       
    }


    @Transactional
    public ApiResponse<RefreshServiceResponseDto> refreshAccessToken(
        String refreshToken
    ){

        Claims claims = jwtService.getAllClaims(refreshToken);
        RefreshToken storedToken = findValidatedRefreshToken(claims);

        if (storedToken.isRevoked()){
            throw new TokenRevokedException();
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now())){
            throw new TokenExpiredException();
        }

        User user = storedToken.getUser();

        // Guards against a stale refresh token outliving an email change on the account
        if (!claims.getSubject().equals(user.getEmail())){
            throw new TokenSubjectMismatchException();
        }

        UserPrincipal userDetails = new UserPrincipal(user);

        // Rotate: mint a new access + refresh token pair, then revoke the old refresh token
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        String newJti = UUID.randomUUID().toString();
        String newRefreshToken = jwtService.generateRefrehToken(userDetails, newJti);
        Instant newRefreshTokenExpiresAt = jwtService.getExpiration(newRefreshToken).toInstant();

        storedToken.setRevoked(true);
        refreshTokenRepo.save(storedToken);
        refreshTokenService.save(newJti, user, newRefreshTokenExpiresAt);

        return new ApiResponse<>(
            true,
            "Access token refreshed",
            mapToRefreshServiceResponseDto(newAccessToken, newRefreshToken, userDetails)
        );
    }

    @Transactional
    public ApiResponse<LogoutResponseDto> logout(String refreshToken) {

        Claims claims = jwtService.getAllClaims(refreshToken);
        RefreshToken storedToken = findValidatedRefreshToken(claims);

        storedToken.setRevoked(true);
        refreshTokenRepo.save(storedToken);

        return new ApiResponse<>(
            true,
            "User has been logged out",
            mapToLogoutResponseDto(claims)
        );

    }

}

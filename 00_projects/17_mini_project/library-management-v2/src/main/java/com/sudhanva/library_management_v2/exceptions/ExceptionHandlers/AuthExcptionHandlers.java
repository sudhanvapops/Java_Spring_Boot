package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.Error.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.NoRefreshTokenExistsException;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.NoRefreshTokenRecordExists;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.NotRefreshTokenException;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.TokenExpiredException;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.TokenRevokedException;
import com.sudhanva.library_management_v2.exceptions.AuthExceptions.TokenSubjectMismatchException;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.PasswordAndConfirmPasswordDoesntMatchException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UserEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UsernameAlreadyExistsException;

import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class AuthExcptionHandlers {

    private ErrorResponseDto mapToErrorResponseDto(
            String message,
            ErrorCode errorCode) {

        return ErrorResponseDto.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private ErrorResponseDto mapToErrorResponseDto(
            Exception exception,
            ErrorCode errorCode) {
        return mapToErrorResponseDto(exception.getMessage(), errorCode);
    }

    @ExceptionHandler(PasswordAndConfirmPasswordDoesntMatchException.class)
    public ResponseEntity<ErrorResponseDto> handlePasswordAndConfirmPasswordDoesntMatchException(
            PasswordAndConfirmPasswordDoesntMatchException exception) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.PASSWORD_AND_CONFIRM_PASSWORD_DOESNT_MATCH));
    }

    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserEmailAlreadyExistsException(
        UserEmailAlreadyExistsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.USER_EMAIL_ALREADY_EXISTS));
    }

    // AuthService.register() reuses MemberEmailAlreadyExistsException for the
    // User account's email uniqueness check, so it's handled here too.
    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberEmailAlreadyExistsException(
        MemberEmailAlreadyExistsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.USER_EMAIL_ALREADY_EXISTS));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameAlreadyExistsException(
        UsernameAlreadyExistsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.USERNAME_ALREADY_EXISTS_EXCEPTION));
    }

    // Covers BadCredentialsException (wrong password) and the UsernameNotFoundException
    // DaoAuthenticationProvider translates it into when no user exists for the given
    // email - both surface identically so login doesn't leak which one was wrong.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
        AuthenticationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapToErrorResponseDto(
                        "Invalid email or password",
                        ErrorCode.UNAUTHORIZED));
    }

    // /api/auth/refresh - no refreshToken cookie was sent
    @ExceptionHandler(NoRefreshTokenExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleNoRefreshTokenExistsException(
        NoRefreshTokenExistsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.NO_REFRESH_TOKEN_EXISTS));
    }

    // /api/auth/refresh - cookie held a valid JWT, but it wasn't issued as a refresh token
    @ExceptionHandler(NotRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleNotRefreshTokenException(
        NotRefreshTokenException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.NOT_REFRESH_TOKEN));
    }

    // /api/auth/refresh - token's jti has no matching RefreshToken row (unknown/forged)
    @ExceptionHandler(NoRefreshTokenRecordExists.class)
    public ResponseEntity<ErrorResponseDto> handleNoRefreshTokenRecordExists(
        NoRefreshTokenRecordExists exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.NO_REFRESH_TOKEN_RECORD_EXISTS));
    }

    // /api/auth/refresh - token was explicitly revoked (e.g. logout)
    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenRevokedException(
        TokenRevokedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.TOKEN_REVOKED));
    }

    // /api/auth/refresh - stored record says this refresh token's validity window has passed
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenExpiredException(
        TokenExpiredException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.TOKEN_EXPIRED));
    }

    // /api/auth/refresh - token's subject no longer matches its associated user
    // (e.g. the account's email changed after this refresh token was issued)
    @ExceptionHandler(TokenSubjectMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenSubjectMismatchException(
        TokenSubjectMismatchException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.TOKEN_SUBJECT_MISMATCH));
    }

    // /api/auth/refresh, /api/auth/logout - cookie held a malformed, tampered, or
    // already-expired JWT. JwtService throws this before a jti is even available to
    // look up, so without this handler it falls through to GlobalExceptionHandler as a 500.
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtException(
        JwtException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapToErrorResponseDto(
                        "Invalid or expired refresh token",
                        ErrorCode.INVALID_REFRESH_TOKEN));
    }

}

package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.Error.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.PasswordAndConfirmPasswordDoesntMatchException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UserEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UsernameAlreadyExistsException;

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

}

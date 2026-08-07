package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.Error.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.PasswordAndConfirmPasswordDoesntMatchException;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UserEmailAlreadyExistsException;

@RestControllerAdvice
public class AuthExcptionHandlers {

    private ErrorResponseDto mapToErrorResponseDto(
            Exception exception,
            ErrorCode errorCode) {

        return ErrorResponseDto.builder()
                .success(false)
                .errorCode(errorCode)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
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

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameAlreadyExistsException(
        MemberEmailAlreadyExistsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.USERNAME_ALREADY_EXISTS_EXCEPTION));
    }




}

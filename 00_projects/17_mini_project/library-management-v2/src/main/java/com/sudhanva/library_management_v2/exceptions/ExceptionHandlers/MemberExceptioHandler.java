package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberNotFoundException;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberInactiveException;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberEmailAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberHasActiveBorrowsException;


@RestControllerAdvice
public class MemberExceptioHandler {
    

    // Utiltiy Methods
    
    private ErrorResponseDto mapToErrorResponseDto(
        Exception exception,
        ErrorCode errorCode
    ){
        return ErrorResponseDto.builder()
            .success(false)
            .errorCode(errorCode)
            .message(exception.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }


    // Handlers
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberNotFound(
        MemberNotFoundException exception
    ){
        ErrorResponseDto response = mapToErrorResponseDto(
            exception,ErrorCode.MEMBER_NOT_FOUND
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MemberInactiveException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberInactive(
        MemberInactiveException exception
    ){
        ErrorResponseDto response = mapToErrorResponseDto(
            exception, ErrorCode.MEMBER_INACTIVE
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberEmailAlreadyExists(
        MemberEmailAlreadyExistsException exception
    ){
        ErrorResponseDto response = mapToErrorResponseDto(
            exception, ErrorCode.MEMBER_EMAIL_ALREADY_EXISTS
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MemberHasActiveBorrowsException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberHasActiveBorrows(
        MemberHasActiveBorrowsException exception
    ){
        ErrorResponseDto response = mapToErrorResponseDto(
            exception, ErrorCode.MEMBER_HAS_ACTIVE_BORROWS
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

}

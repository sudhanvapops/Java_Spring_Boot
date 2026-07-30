package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberNotFoundException;


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

        System.out.println(exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}

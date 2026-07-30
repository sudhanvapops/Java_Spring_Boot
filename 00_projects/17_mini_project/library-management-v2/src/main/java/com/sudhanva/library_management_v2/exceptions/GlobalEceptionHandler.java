package com.sudhanva.library_management_v2.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.ErrorCode;

// casue other vise it expects a view
// This catches everything that wasn't handled by the other advice classes.
@RestControllerAdvice
public class GlobalEceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> globalHandler(
        Exception exception
    ){
        ErrorResponseDto response = ErrorResponseDto.builder()
            .success(false)
            .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
            .message(exception.getMessage())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}


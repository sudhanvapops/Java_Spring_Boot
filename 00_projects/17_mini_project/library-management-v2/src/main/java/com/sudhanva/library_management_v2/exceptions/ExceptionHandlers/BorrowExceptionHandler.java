package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.BorrowExceptions.DuplicateBookRequestException;

@RestControllerAdvice
public class BorrowExceptionHandler {

    private ErrorResponseDto mapToErrorResponseDto(
            Exception exception,
            ErrorCode errorCode
    ) {
        return ErrorResponseDto.builder()
                .success(false)
                .errorCode(errorCode)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DuplicateBookRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateBookRequest(
            DuplicateBookRequestException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.DUPLICATE_BOOK_REQUEST
                ));
    }

}
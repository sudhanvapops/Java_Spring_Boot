package com.sudhanva.library_management_v2.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.ErrorCode;



// casue other vise it expects a view
@RestControllerAdvice
public class GlobalEceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberNotFound(
        MemberNotFoundException exception
    ){
        ErrorResponseDto response = 
            ErrorResponseDto.builder()
                .success(false)
                .errorCode(ErrorCode.MEMBER_NOT_FOUND)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        System.out.println(exception);
        System.out.println(exception.getStackTrace());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}

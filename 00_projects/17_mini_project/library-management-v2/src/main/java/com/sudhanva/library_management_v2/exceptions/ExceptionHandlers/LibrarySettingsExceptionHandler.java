package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.Error.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.LibraryExceptions.NoLibrarySettingsAvailableException;
import com.sudhanva.library_management_v2.exceptions.LibraryExceptions.SettingNotFoundException;
import com.sudhanva.library_management_v2.exceptions.LibraryExceptions.SettingAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.LibraryExceptions.InvalidSettingValueException;

@RestControllerAdvice
public class LibrarySettingsExceptionHandler {

    // Utitly Mathod

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
    

    // Excepitons
    
    @ExceptionHandler(NoLibrarySettingsAvailableException.class)
    public ResponseEntity<ErrorResponseDto> handleNoBooksFound(
        NoLibrarySettingsAvailableException exception
    ){

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapToErrorResponseDto(
                exception,
                ErrorCode.NO_LIBRARY_SETTINGS_AVAILABLE
            ));
    }

    @ExceptionHandler(SettingNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleSettingNotFound(
        SettingNotFoundException exception
    ){

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapToErrorResponseDto(
                exception,
                ErrorCode.SETTING_NOT_FOUND
            ));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidSettingValue(
        NumberFormatException exception
    ){

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapToErrorResponseDto(
                exception,
                ErrorCode.INVALID_SETTING_VALUE
            ));
    }

    @ExceptionHandler(InvalidSettingValueException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidSettingValueException(
        InvalidSettingValueException exception
    ){

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapToErrorResponseDto(
                exception,
                ErrorCode.INVALID_SETTING_VALUE
            ));
    }

    @ExceptionHandler(SettingAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleSettingAlreadyExists(
        SettingAlreadyExistsException exception
    ){

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(mapToErrorResponseDto(
                exception,
                ErrorCode.SETTING_ALREADY_EXISTS
            ));
    }
}

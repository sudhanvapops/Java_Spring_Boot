package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.Error.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookCurrentlyBorrowedException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookInactiveException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookNotFoundException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.InvalidBookCopiesException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.NoBooksFoundException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookNotAvailableException;


// casue other vise it expects a view
@RestControllerAdvice
public class BookExceptionHandler {

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

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleBookNotFound(
            BookNotFoundException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.BOOK_NOT_FOUND
                ));
    }

    @ExceptionHandler(NoBooksFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoBooksFound(
        NoBooksFoundException exception
    ) {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapToErrorResponseDto(
                exception,
                ErrorCode.BOOK_NOT_FOUND
            ));
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleBookAlreadyExists(
            BookAlreadyExistsException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.BOOK_ALREADY_EXISTS
                ));
    }

    @ExceptionHandler(InvalidBookCopiesException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCopies(
            InvalidBookCopiesException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.INVALID_BOOK_COPIES
                ));
    }

    @ExceptionHandler(BookInactiveException.class)
    public ResponseEntity<ErrorResponseDto> handleInactiveBook(
            BookInactiveException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.BOOK_INACTIVE
                ));
    }

    @ExceptionHandler(BookCurrentlyBorrowedException.class)
    public ResponseEntity<ErrorResponseDto> handleBorrowedBook(
            BookCurrentlyBorrowedException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.BOOK_CURRENTLY_BORROWED
                ));
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ErrorResponseDto> handleBookNotAvailable(
            BookNotAvailableException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.BOOK_NOT_AVAILABLE
                ));
    }

}
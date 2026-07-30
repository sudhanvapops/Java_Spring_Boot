package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions.NoBorrowRecordsFoundException;
import com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions.NoUnreturnedBooksFoundException;
import com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions.NoActiveBorrowedBooksException;
import com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions.BookNotBorrowedByMemberException;

@RestControllerAdvice
public class BorrowRecordExceptionHandler {

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

    @ExceptionHandler(NoBorrowRecordsFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoBorrowRecordsFound(
            NoBorrowRecordsFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.NO_BORROW_RECORDS_FOUND
                ));
    }

    @ExceptionHandler(NoUnreturnedBooksFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoUnreturnedBooksFound(
            NoUnreturnedBooksFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.NO_UNRETURNED_BOOKS_FOUND
                ));
    }

    @ExceptionHandler(NoActiveBorrowedBooksException.class)
    public ResponseEntity<ErrorResponseDto> handleNoActiveBorrowedBooks(
            NoActiveBorrowedBooksException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.NO_ACTIVE_BORROWED_BOOKS
                ));
    }

    @ExceptionHandler(BookNotBorrowedByMemberException.class)
    public ResponseEntity<ErrorResponseDto> handleBookNotBorrowedByMember(
            BookNotBorrowedByMemberException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.BOOK_NOT_BORROWED_BY_MEMBER
                ));
    }

}
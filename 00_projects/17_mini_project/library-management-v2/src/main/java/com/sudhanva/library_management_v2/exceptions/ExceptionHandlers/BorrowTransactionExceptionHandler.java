package com.sudhanva.library_management_v2.exceptions.ExceptionHandlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.enums.Error.ErrorCode;
import com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions.BorrowTransactionNotFoundException;
import com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions.NoBorrowTransactionsFoundException;
import com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions.MaxBookLimitExceededException;
import com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions.BookAlreadyBorrowedByMemberException;

@RestControllerAdvice
public class BorrowTransactionExceptionHandler {

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

    @ExceptionHandler(BorrowTransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleBorrowTransactionNotFound(
            BorrowTransactionNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.BORROW_TRANSACTION_NOT_FOUND
                ));
    }

    @ExceptionHandler(NoBorrowTransactionsFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoBorrowTransactionsFound(
            NoBorrowTransactionsFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.NO_BORROW_TRANSACTIONS_FOUND
                ));
    }

    @ExceptionHandler(MaxBookLimitExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMaxBookLimitExceeded(
            MaxBookLimitExceededException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.MAX_BOOK_LIMIT_EXCEEDED
                ));
    }

    @ExceptionHandler(BookAlreadyBorrowedByMemberException.class)
    public ResponseEntity<ErrorResponseDto> handleBookAlreadyBorrowedByMember(
            BookAlreadyBorrowedByMemberException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapToErrorResponseDto(
                        exception,
                        ErrorCode.BOOK_ALREADY_BORROWED_BY_MEMBER
                ));
    }

}
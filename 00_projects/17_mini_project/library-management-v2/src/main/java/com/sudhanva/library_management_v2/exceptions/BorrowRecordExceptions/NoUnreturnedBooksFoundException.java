package com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions;

public class NoUnreturnedBooksFoundException extends RuntimeException {

    public NoUnreturnedBooksFoundException(Long memberId) {
        super("No unreturned books found for member id: " + memberId);
    }
}
package com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions;

public class NoActiveBorrowedBooksException extends RuntimeException {

    public NoActiveBorrowedBooksException(Long memberId) {
        super("Member has no active borrowed books, id: " + memberId);
    }
}
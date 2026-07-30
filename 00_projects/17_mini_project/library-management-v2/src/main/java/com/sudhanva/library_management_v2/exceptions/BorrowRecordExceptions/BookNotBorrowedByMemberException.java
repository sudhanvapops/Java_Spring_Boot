package com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions;

import java.util.List;

public class BookNotBorrowedByMemberException extends RuntimeException {

    public BookNotBorrowedByMemberException(List<Long> bookIds) {
        super("Book(s) " + bookIds + " is not currently borrowed by this member.");
    }
}
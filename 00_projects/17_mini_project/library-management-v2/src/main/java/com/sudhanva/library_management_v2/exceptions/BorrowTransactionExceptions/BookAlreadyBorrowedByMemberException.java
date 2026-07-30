package com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions;

import java.util.List;

public class BookAlreadyBorrowedByMemberException extends RuntimeException {

    public BookAlreadyBorrowedByMemberException(List<Long> bookIds) {
        super("Book(s) already borrowed by member: " + bookIds);
    }
}
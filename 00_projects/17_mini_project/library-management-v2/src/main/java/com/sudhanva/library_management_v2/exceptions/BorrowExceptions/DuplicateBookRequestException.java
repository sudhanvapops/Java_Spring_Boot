package com.sudhanva.library_management_v2.exceptions.BorrowExceptions;

public class DuplicateBookRequestException extends RuntimeException {

    public DuplicateBookRequestException() {
        super("Duplicate book IDs found in request.");
    }
}
package com.sudhanva.library_management_v2.exceptions.BookExceptions;

public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(Long bookId) {
        super("No available copy for book id: " + bookId);
    }
}
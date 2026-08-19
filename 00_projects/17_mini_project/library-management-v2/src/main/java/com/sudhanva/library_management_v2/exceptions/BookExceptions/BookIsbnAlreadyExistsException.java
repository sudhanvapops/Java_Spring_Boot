package com.sudhanva.library_management_v2.exceptions.BookExceptions;


public class BookIsbnAlreadyExistsException extends RuntimeException {

    public BookIsbnAlreadyExistsException(String isbn) {
        super("A book with isbn '" + isbn + "' already exists.\n");
    }

}

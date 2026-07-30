package com.sudhanva.library_management_v2.exceptions.BookExceptions;


public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(
        String name, String author
    ) {
        super("Book '" + name + "' by '" + author + "' already exists.\n");
    }

}
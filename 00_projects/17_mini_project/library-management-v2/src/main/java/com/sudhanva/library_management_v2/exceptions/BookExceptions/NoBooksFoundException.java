package com.sudhanva.library_management_v2.exceptions.BookExceptions;

public class NoBooksFoundException extends RuntimeException {
    public NoBooksFoundException() {
        super("No books available in the library.");
    }
}
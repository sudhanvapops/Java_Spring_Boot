package com.sudhanva.library_management_v2.exceptions.BookExceptions;


public class BookInactiveException extends RuntimeException {
    
    public BookInactiveException() {
        super("Book is Inactive \n");
    }

}

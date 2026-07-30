package com.sudhanva.library_management_v2.exceptions.BookExceptions;


public class BookCurrentlyBorrowedException extends RuntimeException {
    public BookCurrentlyBorrowedException(
    ){
        super("Book is currently borrowed and cannot be deactivated.\n");
    }
}

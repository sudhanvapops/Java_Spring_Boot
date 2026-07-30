package com.sudhanva.library_management_v2.exceptions.BookExceptions;

public class BookAlreadyActiveException extends RuntimeException{
    
    public BookAlreadyActiveException(Long id){
        super("Book is Already Active: "+id);
    }
}

package com.sudhanva.library_management_v2.exceptions.BookExceptions;

public class InvalidBookCopiesException extends RuntimeException{
    
    public InvalidBookCopiesException(
        Integer availableCopies,
        Integer totalCopies
    ) {
        super(
            "Available copies cannot exceed total copies. "+
            "TotalCopies: "+totalCopies+
            " AvailableCopies: "+availableCopies
        );
    }
}

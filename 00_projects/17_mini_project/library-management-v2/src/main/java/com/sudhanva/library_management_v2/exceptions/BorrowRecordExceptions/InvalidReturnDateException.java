package com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions;

import java.time.LocalDateTime;

public class InvalidReturnDateException extends RuntimeException {
    public InvalidReturnDateException(LocalDateTime returnDate) {
        super("Return date cannot be in the future: " + returnDate);
    }
}

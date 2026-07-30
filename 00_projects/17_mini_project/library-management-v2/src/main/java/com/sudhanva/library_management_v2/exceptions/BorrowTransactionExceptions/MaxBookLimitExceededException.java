package com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions;

public class MaxBookLimitExceededException extends RuntimeException {

    public MaxBookLimitExceededException(int maxBooks) {
        super("Max borrowing limit exceeded. A member can borrow at most " + maxBooks + " books.");
    }
}
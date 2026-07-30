package com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions;

public class NoBorrowTransactionsFoundException extends RuntimeException {

    public NoBorrowTransactionsFoundException() {
        super("No borrow transactions found");
    }

    public NoBorrowTransactionsFoundException(Long memberId) {
        super("No borrow transactions found for member id: " + memberId);
    }
}
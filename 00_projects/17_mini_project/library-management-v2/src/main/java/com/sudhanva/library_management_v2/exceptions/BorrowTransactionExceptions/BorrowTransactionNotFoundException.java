package com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions;

public class BorrowTransactionNotFoundException extends RuntimeException {

    public BorrowTransactionNotFoundException(Long id) {
        super("Borrow transaction not found with id: " + id);
    }
}
package com.sudhanva.library_management_v2.exceptions.BorrowRecordExceptions;

public class NoBorrowRecordsFoundException extends RuntimeException {

    public NoBorrowRecordsFoundException() {
        super("No borrow records found");
    }

    public NoBorrowRecordsFoundException(Long memberId) {
        super("No borrow records found for member id: " + memberId);
    }
}
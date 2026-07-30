package com.sudhanva.library_management_v2.enums;

public enum ErrorCode {

    // Memebr
    MEMBER_NOT_FOUND,
    MEMBER_INACTIVE,

     // Book
    BOOK_NOT_FOUND,
    BOOK_INACTIVE,
    BOOK_NOT_AVAILABLE,

    // Borrow
    MAX_BOOK_LIMIT_EXCEEDED,
    DUPLICATE_BOOK_REQUEST,
    BOOK_ALREADY_BORROWED,

    // Return
    BORROW_RECORD_NOT_FOUND,

    // Generic
    VALIDATION_FAILED,
    INTERNAL_SERVER_ERROR
}

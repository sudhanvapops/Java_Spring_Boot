package com.sudhanva.library_management_v2.exceptions.MemberExceptions;

public class MemberHasActiveBorrowsException extends RuntimeException {

    public MemberHasActiveBorrowsException(Long memberId) {
        super("Member has borrowed books that are not yet returned, id: " + memberId);
    }
}
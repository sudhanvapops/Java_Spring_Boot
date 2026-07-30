package com.sudhanva.library_management_v2.exceptions.MemberExceptions;

public class MemberInactiveException extends RuntimeException {

    public MemberInactiveException(Long memberId) {
        super("Member is inactive, id: " + memberId);
    }
}
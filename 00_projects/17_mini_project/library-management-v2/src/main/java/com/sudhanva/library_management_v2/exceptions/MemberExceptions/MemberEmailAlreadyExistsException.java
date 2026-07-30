package com.sudhanva.library_management_v2.exceptions.MemberExceptions;

public class MemberEmailAlreadyExistsException extends RuntimeException {

    public MemberEmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}

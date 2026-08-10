package com.sudhanva.library_management_v2.exceptions.AuthExceptions;

public class TokenSubjectMismatchException extends RuntimeException {
    public TokenSubjectMismatchException(){
        super("Refresh token subject does not match the associated user");
    }
}

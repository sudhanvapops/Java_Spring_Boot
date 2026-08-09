package com.sudhanva.library_management_v2.exceptions.AuthExceptions;

public class NoRefreshTokenExistsException extends RuntimeException {

    public NoRefreshTokenExistsException() {
        super("Refresh Token Doesn't exist");
    }
    
}

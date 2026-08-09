package com.sudhanva.library_management_v2.exceptions.AuthExceptions;

public class TokenRevokedException extends RuntimeException {
    public TokenRevokedException(){
        super("Refresh token has been revoked");
    }
}

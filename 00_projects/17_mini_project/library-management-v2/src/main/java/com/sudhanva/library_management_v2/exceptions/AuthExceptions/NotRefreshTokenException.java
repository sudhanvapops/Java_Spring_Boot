package com.sudhanva.library_management_v2.exceptions.AuthExceptions;

public class NotRefreshTokenException extends RuntimeException {
    public NotRefreshTokenException(){
        super("Token Is Not A Refresh Token");
    }
}

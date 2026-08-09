package com.sudhanva.library_management_v2.exceptions.AuthExceptions;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException(){
        super("Refresh token has expired");
    }
}

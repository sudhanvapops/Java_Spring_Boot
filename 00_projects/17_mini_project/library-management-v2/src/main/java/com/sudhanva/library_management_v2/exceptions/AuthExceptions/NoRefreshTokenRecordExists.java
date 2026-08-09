package com.sudhanva.library_management_v2.exceptions.AuthExceptions;

public class NoRefreshTokenRecordExists extends RuntimeException {

    public NoRefreshTokenRecordExists(String jti) {
        super("No Refresh Token Record Exist: "+jti);
    }
    
}

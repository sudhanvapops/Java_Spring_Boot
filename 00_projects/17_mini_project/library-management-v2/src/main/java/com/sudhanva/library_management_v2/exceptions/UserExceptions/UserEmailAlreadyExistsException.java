package com.sudhanva.library_management_v2.exceptions.UserExceptions;

public class UserEmailAlreadyExistsException extends RuntimeException{

    public UserEmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
    
}

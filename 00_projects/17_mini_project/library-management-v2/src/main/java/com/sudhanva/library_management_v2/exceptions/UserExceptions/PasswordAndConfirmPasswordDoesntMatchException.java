package com.sudhanva.library_management_v2.exceptions.UserExceptions;

public class PasswordAndConfirmPasswordDoesntMatchException extends RuntimeException{

    public PasswordAndConfirmPasswordDoesntMatchException() {
        super("Password and Confirm Password doesnt match");
    }
    
}

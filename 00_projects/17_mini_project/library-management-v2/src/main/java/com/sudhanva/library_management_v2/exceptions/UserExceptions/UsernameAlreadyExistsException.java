package com.sudhanva.library_management_v2.exceptions.UserExceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username){
        super("UserName Already Exits: "+username);
    }

}

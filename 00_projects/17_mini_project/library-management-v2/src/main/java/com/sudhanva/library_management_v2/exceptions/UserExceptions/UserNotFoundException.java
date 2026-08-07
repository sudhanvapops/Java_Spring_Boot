package com.sudhanva.library_management_v2.exceptions.UserExceptions;


public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String msg){
        super("Member not found with: "+msg);
    }

    public static UserNotFoundException byUsername(String username){
        return new UserNotFoundException(username);
    }

    public static UserNotFoundException byEmail(String email){
        return new UserNotFoundException(email);
    }


}

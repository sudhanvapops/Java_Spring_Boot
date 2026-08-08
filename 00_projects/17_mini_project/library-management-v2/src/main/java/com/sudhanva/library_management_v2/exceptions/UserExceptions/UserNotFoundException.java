package com.sudhanva.library_management_v2.exceptions.UserExceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Extends UsernameNotFoundException (not plain RuntimeException) so
// DaoAuthenticationProvider recognizes it during login and converts it into a
// BadCredentialsException, instead of it escaping the AuthenticationManager
// uncaught.
public class UserNotFoundException extends UsernameNotFoundException{

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

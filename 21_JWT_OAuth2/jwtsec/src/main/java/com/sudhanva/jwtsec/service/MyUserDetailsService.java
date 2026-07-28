package com.sudhanva.jwtsec.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sudhanva.jwtsec.model.User;
import com.sudhanva.jwtsec.repo.UserRepo;
import com.sudhanva.jwtsec.security.UserPrincipal;




// This calss is for to tell How to get User from DB to DAO
@Service
public class MyUserDetailsService implements UserDetailsService {


    private final UserRepo userRepo;

    MyUserDetailsService(UserRepo userRepo){
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // My Model User
        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null) {
            System.out.println("\nUser not found: 404 "+ username+"\n");
            throw new UsernameNotFoundException("User not found: 404 "+ username+"");
        }


        return new UserPrincipal(user);

    }
    

}

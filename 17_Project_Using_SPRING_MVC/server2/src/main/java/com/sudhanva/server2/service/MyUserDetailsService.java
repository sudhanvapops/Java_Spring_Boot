package com.sudhanva.server2.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sudhanva.server2.Model.User;
import com.sudhanva.server2.repo.UserRepo;
import com.sudhanva.server2.security.UserPrincipal;


@Service
public class MyUserDetailsService implements UserDetailsService {


    private final UserRepo userRepo;

    MyUserDetailsService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username);

        if(user == null){
            System.out.println("\nUser not found: 404 "+ username+"\n");
            throw new UsernameNotFoundException("User not found: 404 "+ username+"");
        }

        return new UserPrincipal(user);
    }
    

}

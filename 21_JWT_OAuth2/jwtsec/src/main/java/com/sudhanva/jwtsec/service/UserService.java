package com.sudhanva.jwtsec.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sudhanva.jwtsec.model.User;
import com.sudhanva.jwtsec.repo.UserRepo;


@Service
public class UserService {


    final UserRepo userRepo;

    UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    // Here you have a choice to make a bean in config and return it here wire it
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
    public User saveUser(User user){

        System.out.println("\nOld Password: "+user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println("New Password: "+user.getPassword()+"\n");

        return userRepo.save(user);
    }

}

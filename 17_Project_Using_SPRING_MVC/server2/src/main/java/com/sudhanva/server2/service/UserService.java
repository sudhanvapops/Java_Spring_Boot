package com.sudhanva.server2.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sudhanva.server2.Model.User;
import com.sudhanva.server2.repo.UserRepo;

@Service
public class UserService {

    final UserRepo userRepo;

    UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
   
    public User saveUser(User user) {
        
        // validation 

        // save

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        System.out.println("\nOld Password: "+user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println("New Password: "+user.getPassword()+"\n");

        return userRepo.save(user);
    }
    
}

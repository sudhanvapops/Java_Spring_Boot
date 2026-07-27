package com.sudhanva.springsec.service;

import org.springframework.stereotype.Service;

import com.sudhanva.springsec.model.User;
import com.sudhanva.springsec.repo.UserRepo;

@Service
public class UserService {


    final UserRepo userRepo;

    UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    
    public User saveUser(User user){
        return userRepo.save(user);
    }

}

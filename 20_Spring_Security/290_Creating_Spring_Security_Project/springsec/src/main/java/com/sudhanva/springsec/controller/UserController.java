package com.sudhanva.springsec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.springsec.model.User;
import com.sudhanva.springsec.service.UserService;


@RestController
public class UserController {
    
    @Autowired
    UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<User> registUser(
        @RequestBody User user
    ){
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

}

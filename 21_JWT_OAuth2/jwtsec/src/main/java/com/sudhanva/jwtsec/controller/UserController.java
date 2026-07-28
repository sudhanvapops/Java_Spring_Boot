package com.sudhanva.jwtsec.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.jwtsec.model.User;
import com.sudhanva.jwtsec.service.UserService;




@RestController
public class UserController {
    
    final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> registUser(
        @RequestBody User user
    ){
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

}

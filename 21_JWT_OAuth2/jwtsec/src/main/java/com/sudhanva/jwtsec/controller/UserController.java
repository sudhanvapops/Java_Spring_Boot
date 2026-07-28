package com.sudhanva.jwtsec.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.jwtsec.model.User;
import com.sudhanva.jwtsec.service.UserService;




@RestController
public class UserController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    UserController(
        UserService userService,
        AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    
    @PostMapping("/register")
    public ResponseEntity<User> registUser(
        @RequestBody User user
    ){
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }


    @PostMapping("/login")
    public ResponseEntity<Authentication> loginUser(
        @RequestBody User user
    ){
        
        Authentication authenticate = new UsernamePasswordAuthenticationToken(
            user.getUsername(), 
            user.getPassword()
        );

        Authentication authenticated = authenticationManager.authenticate(authenticate);

        if (!authenticated.isAuthenticated()){
            System.out.println("\nPassword Wrong\n");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticated);
        }

        return ResponseEntity.ok(authenticated);
    }

}

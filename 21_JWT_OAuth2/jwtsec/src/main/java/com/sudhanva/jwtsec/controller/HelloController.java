package com.sudhanva.jwtsec.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {
    

    @GetMapping("/")
    public String hello(){
        return "Hello World Session Id: ";
    }

    @GetMapping("/hello")
    public String greet(){
        // Give me the current session. If one doesn't exist, create one.
        // if not used false
        return "Hello World Session Id: ";
    }


}

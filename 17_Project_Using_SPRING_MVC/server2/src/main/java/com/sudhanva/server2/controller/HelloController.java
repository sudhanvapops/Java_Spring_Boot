package com.sudhanva.server2.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class HelloController {

    @GetMapping("/hello")
    public String getMethodName() {
        return new String("Hello");
    }
    
}

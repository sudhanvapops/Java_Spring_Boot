package com.first.di;

import org.springframework.stereotype.Component;

@Component
public class Cpu {
    
    public String getCpu(){
        return "Intel i9";
    }

}

package com.sudhanva.jobapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
public class JobController {
    
    @RequestMapping(value = {"/","home"},method = RequestMethod.GET)
    public String home(){
        return "home";
    }

    @RequestMapping(value = {"addjob"}, method=RequestMethod.GET)
    public String addJob(){
        return "addjob";
    }

}

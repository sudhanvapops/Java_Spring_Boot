package com.sudhanva;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {

        // Making a IOC Conatiner
        ApplicationContext context = new ClassPathXmlApplicationContext();


        Alien obj = (Alien) context.getBean("alien");
        obj.code();

        
    }
}

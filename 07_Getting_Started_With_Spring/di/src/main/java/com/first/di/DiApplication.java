package com.first.di;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;



@SpringBootApplication
public class DiApplication {

	public static void main(String[] args) {
		ApplicationContext context =  SpringApplication.run(DiApplication.class, args);

		System.out.println("\nWelcome Sudhanva. Again!!!");

		// ! Direct Object Creation
		// Alien alien = new Alien();
		// alien.code();

		// Inversion Of Control
		Alien obj = context.getBean(Alien.class);
		obj.code();

		Alien obj1 = context.getBean(Alien.class);
		System.out.println(obj1.equals(obj));
	}

}

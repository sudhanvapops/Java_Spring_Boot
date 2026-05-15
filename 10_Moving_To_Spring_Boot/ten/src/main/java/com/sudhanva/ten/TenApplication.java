package com.sudhanva.ten;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TenApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(TenApplication.class, args);

		Alien alien = context.getBean(Alien.class);
		System.out.println(alien.getAge());
		alien.code();

	}

}

package com.sudhanva.springdatajpa;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.sudhanva.springdatajpa.model.Student;
import com.sudhanva.springdatajpa.repo.StudentRepo;

@SpringBootApplication
public class SpringdatajpaApplication {

	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(SpringdatajpaApplication.class, args);

		StudentRepo studentRepo = ctx.getBean(StudentRepo.class);

		Student s1 = ctx.getBean(Student.class);
		s1.setRollNo(101);
		s1.setName("Sudhanva");
		s1.setMarks(256);

		Student s2 = ctx.getBean(Student.class);
		s2.setRollNo(102);
		s2.setName("Rahul");
		s2.setMarks(220);

		Student s3 = ctx.getBean(Student.class);
		s3.setRollNo(103);
		s3.setName("Priya");
		s3.setMarks(245);

		Student s4 = ctx.getBean(Student.class);
		s4.setRollNo(104);
		s4.setName("Arjun");
		s4.setMarks(230);

		// Save To Db
		// studentRepo.save(s1);
		// studentRepo.save(s2);
		// studentRepo.save(s3);

		// Find All
		// System.out.println(studentRepo.findAll());


		// Find One
		// Student s = studentRepo
				// .findById(101)
				// .orElseThrow(() -> new RuntimeException("Student Not Found"));

		// Or you can use ifPresent

		// studentRepo.findById(103)
		// 		.ifPresentOrElse(
		// 				Student -> System.out.println(Student),
		// 				() -> {
		// 					throw new RuntimeException("Student not found");
		// 				});

		// System.out.println("\n" + s + "\n");


		// Search By Name
		// List<Student> sList = studentRepo.findByName("Sudhanva");
		// System.out.println(sList);

		// Find By Marks
		// List<Student> sMarks = studentRepo.findByMarks(256);
		// System.out.println(sMarks);
		
		// List<Student> sgmarks = studentRepo.findByMarksGreaterThan(250);
		// System.out.println(sgmarks);



		// Update 
		// To update use save
		// s1.setRollNo(101);
		// s1.setName("Sudhanva");
		// s1.setMarks(220);
		// studentRepo.save(s1);
		
		// Delete
		studentRepo.delete(s2);


	}
 
}

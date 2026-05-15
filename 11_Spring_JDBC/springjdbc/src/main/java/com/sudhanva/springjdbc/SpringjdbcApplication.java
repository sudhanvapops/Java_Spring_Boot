package com.sudhanva.springjdbc;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.sudhanva.springjdbc.model.Student;
import com.sudhanva.springjdbc.service.StudentService;

@SpringBootApplication
public class SpringjdbcApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(SpringjdbcApplication.class, args);

		StudentService studentService = context.getBean(StudentService.class);


		Student student = context.getBean(Student.class);
		student.setRollNo(1);
		student.setName("Sudhanva S");
		student.setMarks(86);


		studentService.addStudent(student);


		List<Student> students = studentService.getStudents();
		System.out.println(students);

	}

}

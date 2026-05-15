package com.sudhanva.springjdbc.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sudhanva.springjdbc.model.Student;

@Repository
public class StudentDAO {
    
    public void save(Student s){
        System.out.println("Added");
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        return students;
    }



}

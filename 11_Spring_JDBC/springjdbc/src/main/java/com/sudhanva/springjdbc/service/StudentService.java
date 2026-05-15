package com.sudhanva.springjdbc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.springjdbc.model.Student;
import com.sudhanva.springjdbc.repo.StudentDAO;

@Service
public class StudentService {

    private StudentDAO studentDAO;

    public StudentDAO getStudentDAO() {
        return studentDAO;
    }

    @Autowired
    public void setStudentDAO(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public void addStudent(Student s) {
        studentDAO.save(s);
    }

    public List<Student> getStudents() {
        return studentDAO.findAll();
    }

}

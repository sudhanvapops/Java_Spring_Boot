package com.sudhanva.springdatajpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudhanva.springdatajpa.model.Student;


@Repository
public interface StudentRepo extends JpaRepository<Student,Integer>{
    
}

package com.sudhanva.springdatajpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sudhanva.springdatajpa.model.Student;
import java.util.List;



@Repository
public interface StudentRepo extends JpaRepository<Student,Integer>{
    
    // JPQL
    @Query("select s from Student s where s.name = ?1")
    List<Student> findByName(String name);
    
    List<Student> findByMarks(int marks);

    List<Student> findByMarksGreaterThan(int marks);

}

package com.sudhanva.library_management_v2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudhanva.library_management_v2.Model.Book;
import java.util.List;


@Repository
public interface BookRepo extends JpaRepository<Book,Long>{
    
    Book findByNameAndAuthor(String name, String author);
    Boolean existsByName(String name);
    List<Book> findByAuthor(String author);
    List<Book> findByName(String name);

}

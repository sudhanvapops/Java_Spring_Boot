package com.sudhanva.library_management_v2.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudhanva.library_management_v2.Model.Book;


@Repository
public interface BookRepo extends JpaRepository<Book,Long>{
    
    Optional<Book> findByNameAndAuthor(String name, String author);
    Boolean existsByName(String name);
    List<Book> findByAuthor(String author);
    List<Book> findByName(String name);
    Optional<Book> findByIsbn(String isbn);

    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // @Query("SELECT b FROM Book b WHERE b.id IN :ids")
    // List<Book> findAllById(@Param("ids") List<Long> ids);

}

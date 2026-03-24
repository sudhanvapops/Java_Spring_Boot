package com.sudhanva.library.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Check;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_name",nullable = false)
    private String bookName;

    @Column(nullable = false,unique = true)
    private String isbn;

    // Book is OWNER of relationship
    // Set of auhors
    // books filed
    // the field name that owns the relationship in the other entity
    @ManyToMany
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();


    @Column(
        name = "total_copies",
        nullable = false
    )
    private Integer totalCopies;

    @Column(
        name = "available_copies",
        nullable = false
    )
    private Integer availableCopies;

    

    public Book(Long id, String bookName, String isbn, Set<Author> authors, Integer totalCopies,
            Integer availableCopies) {
        this.id = id;
        this.bookName = bookName;
        this.isbn = isbn;
        this.authors = authors;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // --- Helper Methods (important for relationships) ---
    public void addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this); // keep both sides in sync
    }

    public void removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBookName() {
        return bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public Set<Author> getAuthors() {
        return authors;
    }
    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }
    public Integer getTotalCopies() {
        return totalCopies;
    }
     public void setTotalCopies(Integer totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        this.totalCopies = totalCopies;
    }
    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        if (availableCopies < 0 || availableCopies > this.totalCopies) {
            throw new IllegalArgumentException("Invalid available copies");
        }
        this.availableCopies = availableCopies;
    }
    
    @Override
    public String toString() {
        return "Book [id=" + id + ", bookName=" + bookName + ", isbn=" + isbn + ", authors=" + authors
                + ", totalCopies=" + totalCopies + ", availableCopies=" + availableCopies + "]";
    }
    
    
}

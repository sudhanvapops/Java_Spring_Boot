package com.sudhanva.library.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    // Basic Fields

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Column(nullable = false, unique = true, updatable = false)
    private String isbn;

    // Relations

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

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    // List cause records dont have to be unique
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    // Copy Fields

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;


    // --- Helper Methods (important for relationships) ---
    public void addAuthor(Author author) {

        if (this.authors.contains(author))
            return;

        this.authors.add(author);
        author.getBooks().add(this); // keep both sides in sync
    }

    public void removeAuthor(Author author) {

        if (!this.authors.contains(author))
            return;
        this.authors.remove(author);
        author.getBooks().remove(this);
    }


    // ? I dont know why this is there study more
    public void addBorrowRecord(BorrowRecord record) {
        if (borrowRecords.contains(record))
            return;

        borrowRecords.add(record);
        record.setBook(this);
    }


    public void removeBorrowRecord(BorrowRecord record) {
        if (!borrowRecords.contains(record))
            return;

        borrowRecords.remove(record);
        record.setBook(null);
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
        if (this.isbn != null && !this.isbn.equals(isbn)) {
            throw new IllegalStateException("isbn number cannot be changed");
        }
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
        if (availableCopies < 0 || (this.totalCopies != null && availableCopies > this.totalCopies)) {
            throw new IllegalArgumentException("Invalid available copies");
        }
        this.availableCopies = availableCopies;
    }
    

    @Override
    public String toString() {
        return "Book [id=" + id + ", bookName=" + bookName + ", isbn=" + isbn
                + ", totalCopies=" + totalCopies + ", availableCopies=" + availableCopies + "]";
    }

    // Constrctors

    public Book() {
    }

    public Book(
            String bookName,
            String isbn,
            Set<Author> authors,
            Integer totalCopies,
            Integer availableCopies) {
        setBookName(bookName);
        setIsbn(isbn);
        setAuthors(authors);
        setTotalCopies(totalCopies);
        setAvailableCopies(availableCopies);
    }


    // Equals and hash code

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Book other = (Book) obj;
        if (isbn == null) {
            if (other.isbn != null)
                return false;
        } else if (!isbn.equals(other.isbn))
            return false;
        return true;
    }


    

}

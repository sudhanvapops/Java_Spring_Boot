package com.sudhanva.library.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // fields
    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(unique = true, nullable = true)
    private String email;

    @Column(nullable = true)
    private String nationality;

    // relationship
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Author [id=" + id + ", authorName=" + authorName + ", email=" + email + ", nationality=" + nationality
                + "]";
    }

    // Constrctors

    public Author() {
    }

    public Author(String authorName, String email, String nationality, Set<Book> books) {
        this.authorName = authorName;
        this.email = email;
        this.nationality = nationality;
        this.books = books;
    }

    // Equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Author))
            return false;

        Author other = (Author) o;

        // If email exists use it as business identity
        if (this.email != null && other.email != null) {
            return this.email.equals(other.email);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 31;
    }

}
